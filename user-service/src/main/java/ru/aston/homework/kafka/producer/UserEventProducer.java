package ru.aston.homework.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.aston.homework.kafka.dto.UserEvent;

@Component
public class UserEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(UserEventProducer.class);

    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public UserEventProducer(
            KafkaTemplate<String, UserEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(UserEvent event) {
        log.debug(
                "Sending user event: email={}, operation={}",
                event.email(),
                event.operation()
        );

        kafkaTemplate.send(TOPIC, event.email(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error(
                                "Failed to send user event: email={}, operation={}",
                                event.email(),
                                event.operation(),
                                exception
                        );
                        return;
                    }

                    log.info(
                            "User event sent: topic={}, partition={}, offset={}, email={}, operation={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event.email(),
                            event.operation()
                    );
                });
    }
}
