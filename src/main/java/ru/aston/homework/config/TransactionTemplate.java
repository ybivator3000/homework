package ru.aston.homework.config;

import ru.aston.homework.exception.ServiceException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionTemplate {

    public <T> T execute(Function<Session, T> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = action.apply(session);
                tx.commit();
                return result;
            } catch (ServiceException e) {
                tx.rollback();
                throw e;
            } catch (Exception e) {
                tx.rollback();
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
                throw new ServiceException("Read failed", e);
            }
        }
    }
}