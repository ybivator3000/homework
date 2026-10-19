package ru.aston.homework.common.dao;

import ru.aston.homework.entity.User;
import org.hibernate.Session;

import java.util.Optional;


public interface UserDao extends GenericDao<User, Long> {
    Optional<User> findByEmail(Session session, String email);
}
