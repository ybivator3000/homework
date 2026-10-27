package ru.aston.homework.dao.user;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.aston.homework.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplTest {

    @Container
    private static final PostgreSQLContainer POSTGRE_SQL = new PostgreSQLContainer(
            "postgres:16-alpine"
    )
            .withDatabaseName("user_service_test")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
    private UserDaoImpl userDao;

    @BeforeAll
    static void setUpSessionFactory() {
        Configuration configuration = new Configuration();

        configuration.setProperty(
                "hibernate.connection.url",
                POSTGRE_SQL.getJdbcUrl()
        );

        configuration.setProperty(
                "hibernate.connection.username",
                POSTGRE_SQL.getUsername()
        );

        configuration.setProperty(
                "hibernate.connection.password",
                POSTGRE_SQL.getPassword()
        );

        configuration.setProperty(
                "hibernate.hbm2ddl.auto",
                "create-drop"
        );

        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    @BeforeEach
    void setUp() {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        userDao = new UserDaoImpl();
    }

    @AfterEach
    void tearDown() {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Test
    void createShouldPersistUserInDatabase() {
        // Arrange
        User user = new User("Anna", "anna@example.com", 22);

        // Act
        User createdUser = userDao.create(session, user);

        session.flush();

        Long userId = createdUser.getId();

        // Assert
        assertNotNull(userId);

        session.clear();

        User savedUser = session.find(User.class, userId);

        assertNotNull(savedUser);
        assertEquals("Anna", savedUser.getName());
        assertEquals("anna@example.com", savedUser.getEmail());
        assertEquals(22, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void getByIdShouldReturnUserWhenExists() {
        // Arrange
        User user = new User("Maria", "maria@example.com", 25);

        session.persist(user);
        session.flush();

        Long userId = user.getId();
        session.clear();

        // Act
        Optional<User> result = userDao.getById(session, userId);

        // Assert
        assertTrue(result.isPresent());

        User foundUser = result.orElseThrow();

        assertEquals(userId, foundUser.getId());
        assertEquals("Maria", foundUser.getName());
        assertEquals("maria@example.com", foundUser.getEmail());
        assertEquals(25, foundUser.getAge());
    }

    @Test
    void getByIdShouldReturnEmptyWhenUserDoesNotExist() {
        // Arrange
        Long nonExistingId = Long.MAX_VALUE;

        // Act
        Optional<User> result = userDao.getById(session, nonExistingId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllShouldReturnAllUsers() {
        // Arrange
        User anna = new User("Anna", "anna@example.com", 22);
        User maria = new User("Maria", "maria@example.com", 25);

        session.persist(anna);
        session.persist(maria);

        session.flush();
        session.clear();

        // Act
        List<User> users = userDao.getAll(session);

        // Assert
        assertEquals(2, users.size());
        assertTrue(users
                .stream()
                .anyMatch(user -> user
                        .getEmail()
                        .equals("anna@example.com")));
        assertTrue(users
                .stream()
                .anyMatch(user -> user
                        .getEmail()
                        .equals("maria@example.com")));
    }

    @Test
    void getAllShouldReturnEmptyListWhenNoUsersExist() {
        // Act
        List<User> users = userDao.getAll(session);

        // Assert
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void updateShouldPersistChangesForDetachedUser() {
        // Arrange
        User user = new User("Anna", "anna@example.com", 22);

        session.persist(user);
        session.flush();

        Long userId = user.getId();
        session.clear();

        user.setName("Anna-Luiza");
        user.setEmail("anna-luiza@example.com");
        user.setAge(23);

        // Act
        User updatedUser = userDao.update(session, user);

        session.flush();
        session.clear();

        // Assert
        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertNotSame(user, updatedUser);

        User savedUser = session.find(User.class, userId);

        assertNotNull(savedUser);
        assertEquals("Anna-Luiza", savedUser.getName());
        assertEquals("anna-luiza@example.com", savedUser.getEmail());
        assertEquals(23, savedUser.getAge());
    }

    @Test
    void deleteShouldRemoveUserFromDatabase() {
        // Arrange
        User user = new User("Alex", "alex@example.com", 28);

        session.persist(user);
        session.flush();

        Long userId = user.getId();
        session.clear();

        User savedUser = session.find(User.class, userId);
        assertNotNull(savedUser);

        // Act
        userDao.delete(session, savedUser);

        session.flush();
        session.clear();

        // Assert
        User deletedUser = session.find(User.class, userId);

        assertNull(deletedUser);
    }

    @Test
    void findByEmailShouldReturnUserWhenEmailExists() {
        // Arrange
        User user = new User("Elena", "elena@example.com", 30);

        session.persist(user);
        session.flush();

        Long userId = user.getId();
        session.clear();

        // Act
        Optional<User> result = userDao.findByEmail(session, "elena@example.com");

        // Assert
        assertTrue(result.isPresent());

        User foundUser = result.orElseThrow();

        assertEquals(userId, foundUser.getId());
        assertEquals("Elena", foundUser.getName());
        assertEquals("elena@example.com", foundUser.getEmail());
        assertEquals(30, foundUser.getAge());
    }

    @Test
    void findByEmailShouldIgnoreCase() {
        // Arrange
        User user = new User("Olga", "olga@example.com", 27);

        session.persist(user);
        session.flush();
        session.clear();

        // Act
        Optional<User> result = userDao.findByEmail(session, "OLGA@EXAMPLE.COM");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("olga@example.com", result
                .orElseThrow()
                .getEmail());
    }

    @Test
    void findByEmailShouldReturnEmptyWhenEmailDoesNotExist() {
        // Arrange
        User user = new User("Max", "max@example.com", 37);

        session.persist(user);
        session.flush();
        session.clear();

        // Act
        Optional<User> result = userDao.findByEmail(session, "noname@example.com");

        // Assert
        assertTrue(result.isEmpty());
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
