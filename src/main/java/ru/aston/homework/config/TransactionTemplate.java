package ru.aston.homework.config;

import ru.aston.homework.exception.ServiceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionTemplate {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionTemplate.class);

    public <T> T execute(Function<Session, T> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                T result = action.apply(session);
                tx.commit();
                return result;
            } catch (ServiceException e) {
                rollback(tx);
                throw e;
            } catch (Exception e) {
                log.error("Transaction failed", e);
                rollback(tx);
                throw new ServiceException("Transaction failed", e);
            }
        }
    }

    public void executeVoid(Consumer<Session> action) {
        execute(session -> {
            action.accept(session);
            return null;
        });
    }

    public <T> T executeReadOnly(Function<Session, T> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                return action.apply(session);
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                log.error("Read operation failed", e);
                throw new ServiceException("Read failed", e);
            }
        }
    }

    private void rollback(Transaction tx) {
        try {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } catch (Exception rollbackException) {
            log.error("Failed to rollback transaction", rollbackException);
        }
    }
}
