package ru.aston.homework.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.entity.User;

public class HibernateUtil {

    private static final Logger log =
            LoggerFactory.getLogger(HibernateUtil.class);

    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            sessionFactory = createSessionFactory();
        }

        return sessionFactory;
    }

    private static SessionFactory createSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();

            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            if (dbUrl == null || dbUrl.isBlank()
                    || dbUser == null || dbUser.isBlank()
                    || dbPassword == null || dbPassword.isBlank()) {
                throw new IllegalStateException(
                        "Database environment variables are not configured"
                );
            }

            configuration.setProperty("hibernate.connection.url", dbUrl);
            configuration.setProperty("hibernate.connection.username", dbUser);
            configuration.setProperty("hibernate.connection.password", dbPassword);

            configuration.addAnnotatedClass(User.class);

            return configuration.buildSessionFactory();
        } catch (RuntimeException e) {
            log.error("Failed to initialize Hibernate SessionFactory", e);
            throw e;
        }
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            try {
                sessionFactory.close();
                log.info("Hibernate SessionFactory closed");
            } catch (RuntimeException e) {
                log.error("Failed to close Hibernate SessionFactory", e);
                throw e;
            }
        }
    }
}
