package ru.practicum.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.topics")
public class TopicsProperties {
    private String statsUserActionV1;
    private String statsEventsSimilarityV1;
}