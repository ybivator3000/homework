package ru.aston.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = createSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration().configure();

        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUrl == null || dbUrl.isBlank()
                || dbUser == null || dbUser.isBlank()
                || dbPassword == null || dbPassword.isBlank()) {
            throw new IllegalStateException("Database environment variables are not configured");
        }

        configuration.setProperty("hibernate.connection.url", dbUrl);
        configuration.setProperty("hibernate.connection.username", dbUser);
        configuration.setProperty("hibernate.connection.password", dbPassword);

        return configuration.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
