package ru.aston.homework.service.impl;

import ru.aston.homework.config.TransactionTemplate;
import ru.aston.homework.dao.user.UserDao;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.entity.User;
import ru.aston.homework.exception.ServiceException;
import ru.aston.homework.service.UserService;
import ru.aston.homework.util.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final UserValidator validator;
    private final TransactionTemplate txTemplate;

    public UserServiceImpl(UserDao userDao,
                           UserValidator validator,
                           TransactionTemplate txTemplate) {
        this.userDao = userDao;
        this.validator = validator;
        this.txTemplate = txTemplate;
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        log.debug("Creating user: email={}", request.email());
        validator.validate(request);

        return txTemplate.execute(session -> {
            if (userDao.findByEmail(session, request.email().toLowerCase()).isPresent()) {
                log.warn("Create user failed: email already taken email={}", request.email());
                throw new ServiceException("Email already taken: " + request.email());
            }

            User saved = userDao.create(session, toEntity(request));
            log.info("User created: id={}, email={}", saved.getId(), saved.getEmail());
            return toResponse(saved);
        });
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        log.debug("Updating user: id={}", request.id());
        validator.validate(request);

        return txTemplate.execute(session -> {
            User user = userDao.getById(session, request.id())
                    .orElseThrow(() -> {
                        log.warn("Update user failed: not found id={}", request.id());
                        return new ServiceException("User not found: id=" + request.id());
                    });

            if (!user.getEmail().equalsIgnoreCase(request.email())) {
                userDao.findByEmail(session, request.email().toLowerCase()).ifPresent(other -> {
                    if (!other.getId().equals(user.getId())) {
                        log.warn("Update user failed: email taken email={}", request.email());
                        throw new ServiceException("Email already taken: " + request.email());
                    }
                });
            }

            user.setName(request.name());
            user.setEmail(request.email().toLowerCase());
            user.setAge(request.age());

            User updated = userDao.update(session, user);
            log.info("User updated: id={}, email={}", updated.getId(), updated.getEmail());
            return toResponse(updated);
        });
    }

    @Override
    public Optional<UserResponse> findById(Long id) {
        log.debug("Finding user: id={}", id);
        return txTemplate.executeReadOnly(session ->
                userDao.getById(session, id).map(this::toResponse)
        );
    }

    @Override
    public List<UserResponse> findAll() {
        log.debug("Finding all users");
        return txTemplate.executeReadOnly(session -> {
            List<UserResponse> users = userDao.getAll(session).stream()
                    .map(this::toResponse)
                    .toList();
            log.debug("Found {} users", users.size());
            return users;
        });
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting user: id={}", id);
        txTemplate.executeVoid(session -> {
            User user = userDao.getById(session, id)
                    .orElseThrow(() -> {
                        log.warn("Delete user failed: not found id={}", id);
                        return new ServiceException("User not found: id=" + id);
                    });
            userDao.delete(session, user);
            log.info("User deleted: id={}", id);
        });
    }
    private User toEntity(UserCreateRequest request) {
        return new User(
                request.name(),
                request.email().toLowerCase(),
                request.age()
        );
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}