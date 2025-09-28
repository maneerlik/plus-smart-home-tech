package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.storage.SensorSnapshotStorage;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final KafkaClient kafkaClient;
    private final SensorSnapshotStorage storage;


    public void start() {
        Consumer<String, SpecificRecordBase> consumer = kafkaClient.getConsumer();
        Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaClient.getTopicsProperties().getTelemetrySensorsV1()));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(kafkaClient.getPollTimeout());

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    handleRecord(record, producer);
                    manageOffsets(record, count, consumer);
                    count++;
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Error during processing of events from sensors", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Close consumer");
                consumer.close();

                log.info("Close producer");
                producer.close();
            }
        }
    }

    private void manageOffsets(
            ConsumerRecord<String, SpecificRecordBase> record,
            int count,
            Consumer<String, SpecificRecordBase> consumer
    ) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (Objects.nonNull(exception)) log.warn("Error during offset fixing: {}", offsets, exception);
            });
        }
    }

    private void handleRecord(
            ConsumerRecord<String, SpecificRecordBase> record,
            Producer<String, SpecificRecordBase> producer
    ) {
        if (record.value() instanceof SensorEventAvro event) {
            Optional<SensorsSnapshotAvro> updatedSnapshot = storage.updateSnapshot(event);

            updatedSnapshot.ifPresent(snapshot -> {
                long timestamp = snapshot.getTimestamp().toEpochMilli();

                ProducerRecord<String, SpecificRecordBase> snapshotRecord = new ProducerRecord<>(
                        kafkaClient.getTopicsProperties().getTelemetrySnapshotsV1(),
                        null,
                        timestamp,
                        snapshot.getHubId(),
                        snapshot
                );

                producer.send(snapshotRecord, (metadata, ex) -> {
                    if (Objects.nonNull(ex)) {
                        log.error("Error when sending a snapshot for the hub {}: {}",
                                snapshot.getHubId(), ex.getMessage(), ex);
                    } else {
                        log.info("Snapshot for the hub {} successfully submitted: partition={}, offset={}",
                                snapshot.getHubId(), metadata.partition(), metadata.offset());
                    }
                });
            });
        } else {
            log.warn("Unknown message received: {}", record.value());
        }
    }
}
