package ru.yandex.practicum.collector.service.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.config.KafkaTopicsProperties;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
@RequiredArgsConstructor
public class EventProducer {
    private final KafkaTemplate<String, SpecificRecordBase> producer;
    private final KafkaTopicsProperties kafkaTopics;


    private void send(String topic, SpecificRecordBase event, long timestamp, String key) {
        producer.send(new ProducerRecord<>(topic, null, timestamp, key, event));
    }

    public void sendSensorEvent(SpecificRecordBase event) {
        SensorEventAvro avroEvent = (SensorEventAvro) event;
        String hubId = avroEvent.getHubId();
        long timestamp = avroEvent.getTimestamp().toEpochMilli();
        send(kafkaTopics.getTelemetrySensorsV1(), event, timestamp, hubId);
    }

    public void sendHubEvent(SpecificRecordBase event) {
        HubEventAvro avroEvent = (HubEventAvro) event;
        String hubId = avroEvent.getHubId();
        long timestamp = avroEvent.getTimestamp().toEpochMilli();
        send(kafkaTopics.getTelemetryHubsV1(), event, timestamp, hubId);
    }
}
