package ru.yandex.practicum.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicsProperties {
    private String telemetryHubsV1;
    private String telemetrySnapshotsV1;
}
