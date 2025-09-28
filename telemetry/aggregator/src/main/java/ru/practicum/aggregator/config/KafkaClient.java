package ru.practicum.aggregator.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import java.time.Duration;

public interface KafkaClient {
    Consumer<String, SpecificRecordBase> getConsumer();

    Producer<String, SpecificRecordBase> getProducer();

    Duration getPollTimeout();

    KafkaTopicsProperties getTopicsProperties();
}
