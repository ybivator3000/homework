package ru.aston.homework.dao.user;

import org.hibernate.Session;
import ru.aston.homework.entity.User;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    @Override
    public User create(Session session, User entity) {
        session.persist(entity);
        return entity;
    }

    @Override
    public Optional<User> getById(Session session, Long id) {
        return Optional.ofNullable(session.find(User.class, id));
    }

    @Override
    public List<User> getAll(Session session) {
        return session
                .createQuery("FROM User", User.class)
                .getResultList();
    }

    @Override
    public User update(Session session, User entity) {
        return session.merge(entity);
    }

    @Override
    public void delete(Session session, User entity) {
        session.remove(entity);
    }

    @Override
    public Optional<User> findByEmail(Session session, String email) {
        return session
                .createQuery(
                        "FROM User u WHERE lower(u.email) = lower(:email)", User.class)
                .setParameter("email", email)
                .uniqueResultOptional();
    }
}
