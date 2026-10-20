package ru.aston.homework.common.dao;

import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {

    Optional<T> getById(Session session, ID id);

    List<T> getAll(Session session);

    T update(Session session, T entity);

    void delete(Session session, T entity);

    T create(Session session, T entity);
}
