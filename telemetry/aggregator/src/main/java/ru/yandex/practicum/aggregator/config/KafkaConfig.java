package ru.yandex.practicum.aggregator.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.serializer.SensorEventDeserializer;

import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group}")
    private String group;

    @Value("${kafka.consumer.poll.timeout}")
    private long pollTimeout;

    private final KafkaTopicsProperties topicsProperties;

    private Producer<String, SpecificRecordBase> kafkaProducer() {
        Properties config = new Properties();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        return new KafkaProducer<>(config);
    }

    private KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {
        Properties config = new Properties();

        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getCanonicalName());
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group);

        return new KafkaConsumer<>(config);
    }

    @Bean
    @Scope("prototype")
    KafkaClient getClient() {
        return new KafkaClient() {
            private Consumer<String, SpecificRecordBase> consumer;
            private Producer<String, SpecificRecordBase> producer;


            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                if (Objects.isNull(consumer)) consumer = kafkaConsumer();
                return consumer;
            }

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (Objects.isNull(producer)) producer = kafkaProducer();
                return producer;
            }

            @Override
            public Duration getPollTimeout() {
                return Duration.ofMillis(pollTimeout);
            }

            @Override
            public KafkaTopicsProperties getTopicsProperties() {
                return topicsProperties;
            }
        };
    }
}
