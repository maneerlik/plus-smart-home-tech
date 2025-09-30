package ru.yandex.practicum.analyzer.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;

import java.time.Duration;

public interface KafkaClient {
    Consumer<String, SpecificRecordBase> getConsumerHub();

    Consumer<String, SpecificRecordBase> getConsumerSnapshot();

    Duration getPollTimeout();

    KafkaTopicsProperties getTopicsProperties();
}
