package ru.aston.homework.common.dao;

import org.hibernate.Session;

import java.util.List;

public interface GenericDao<T, ID> {

    void create(Session session, T entity);
    T getById(Session session, ID id);
    List<T> getAll(Session session);
    void update(Session session, T entity);
    void delete(Session session, T entity);
}