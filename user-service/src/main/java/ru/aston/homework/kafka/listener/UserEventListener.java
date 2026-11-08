package ru.aston.homework.kafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.aston.homework.kafka.dto.UserEvent;
import ru.aston.homework.kafka.producer.UserEventProducer;

@Component
public class UserEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(UserEventListener.class);

    private final UserEventProducer userEventProducer;

    public UserEventListener(UserEventProducer userEventProducer) {
        this.userEventProducer = userEventProducer;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserEvent(UserEvent event) {
        log.info(
                "Processing user event after transaction commit: email={}, operation={}",
                event.email(),
                event.operation()
        );

        userEventProducer.send(event);
    }
}
