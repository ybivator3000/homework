package ru.aston.homework.service.impl;

import ru.aston.homework.dto.*;
import ru.aston.homework.config.TransactionTemplate;
import ru.aston.homework.util.UserValidator;
import ru.aston.homework.common.dao.UserDao;
import ru.aston.homework.service.*;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserValidator validator;
    private final TransactionTemplate txTemplate;

    public UserServiceImpl(UserDao userDao, UserValidator validator, TransactionTemplate txTemplate) {
        this.userDao = userDao;
        this.validator = validator;
        this.txTemplate = txTemplate;
        }

    @Override
    public UserResponse create(UserCreateRequest request) {
        return null;
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        return null;
    }

    @Override
    public Optional<UserResponse> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<UserResponse> findAll() {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}