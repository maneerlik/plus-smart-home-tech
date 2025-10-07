package ru.yandex.practicum.aggregator.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import java.time.Duration;

/**
 * Интерфейс предоставляет доступ к клиентским компонентам Apache Kafka, продюсеру, консьюмеру
 * и вспомогательным параметрам конфигурации
 * <p>
 * Содержит методы для реализации логики создания и управления экземплярами Kafka-клиентов.
 * Обычно используется в связке с Spring-контекстом, где реализация создаётся как прототипный бин
 * (с аннотацией {@code @Scope("prototype")})
 * </p>
 *
 * @see org.apache.kafka.clients.producer.Producer
 * @see org.apache.kafka.clients.consumer.Consumer
 * @see java.time.Duration
 */

public interface KafkaClient {
    Consumer<String, SpecificRecordBase> getConsumer();

    Producer<String, SpecificRecordBase> getProducer();

    Duration getPollTimeout();

    KafkaTopicsProperties getTopicsProperties();
}
