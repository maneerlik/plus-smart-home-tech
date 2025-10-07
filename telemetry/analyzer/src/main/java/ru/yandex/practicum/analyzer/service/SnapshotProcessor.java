package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final KafkaClient kafkaClient;
    private final SnapshotAnalyzer snapshotAnalyzer;


    public void start() {
        Consumer<String, SpecificRecordBase> consumer = kafkaClient.getConsumerSnapshot();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaClient.getTopicsProperties().getTelemetrySnapshotsV1()));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(kafkaClient.getPollTimeout());

                if (!records.isEmpty()) {
                    AtomicInteger index = new AtomicInteger(0);
                    records.forEach(record -> {
                        handleRecord(record);
                        manageOffsets(record, index.getAndIncrement(), consumer);
                    });

                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Error during processing of events from sensors", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Close consumer");
                consumer.close();
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

        if(count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (Objects.nonNull(exception)) log.warn("Error during offset fixing: {}", offsets, exception);
            });
        }
    }

    private void handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        if (record.value() instanceof SensorsSnapshotAvro snapshot) {
            log.info("Received snapshot: hubId={}, states={}", snapshot.getHubId(), snapshot.getSensorsState());
            snapshotAnalyzer.handleSnapshot(snapshot);
        }
    }
}
