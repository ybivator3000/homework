
package ru.aston.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic userEventsDlt() {
        return TopicBuilder.name("user-events-dlt")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
