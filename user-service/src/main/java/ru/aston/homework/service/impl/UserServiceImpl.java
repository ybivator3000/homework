package ru.aston.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.entity.User;
import ru.aston.homework.exception.EmailAlreadyTakenException;
import ru.aston.homework.exception.UserNotFoundException;
import ru.aston.homework.kafka.dto.UserEvent;
import ru.aston.homework.repository.UserRepository;
import ru.aston.homework.service.UserService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        log.debug("Creating user: email={}", request.email());

        if (userRepository
                .findByEmailIgnoreCase(request.email())
                .isPresent()) {
            log.warn(
                    "Create user failed: email already taken email={}",
                    request.email()
            );
            throw new EmailAlreadyTakenException(request.email());
        }

        User saved = userRepository.saveAndFlush(toEntity(request));

        eventPublisher.publishEvent(
                new UserEvent(saved.getEmail(), UserEvent.Operation.CREATE)
        );

        log.info(
                "User created: id={}, email={}",
                saved.getId(),
                saved.getEmail()
        );

        return toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse update(UserUpdateRequest request) {
        log.debug("Updating user: id={}", request.id());

        User user = userRepository
                .findById(request.id())
                .orElseThrow(() -> {
                    log.warn(
                            "Update user failed: not found id={}",
                            request.id()
                    );
                    return new UserNotFoundException(request.id());
                });

        if (!user
                .getEmail()
                .equalsIgnoreCase(request.email())) {
            userRepository
                    .findByEmailIgnoreCase(request.email())
                    .ifPresent(other -> {
                        if (!other.getId().equals(user.getId())) {
                            log.warn(
                                    "Update user failed: email taken email={}",
                                    request.email()
                            );
                            throw new EmailAlreadyTakenException(
                                    request.email()
                            );
                        }
                    });
        }

        user.setName(request.name());
        user.setEmail(request.email().toLowerCase(Locale.ROOT));
        user.setAge(request.age());

        User updated = userRepository.saveAndFlush(user);

        log.info(
                "User updated: id={}, email={}",
                updated.getId(),
                updated.getEmail()
        );

        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findById(Long id) {
        log.debug("Finding user: id={}", id);

        return userRepository
                .findById(id)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        log.debug("Finding all users");

        List<UserResponse> users = userRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        log.debug("Found {} users", users.size());

        return users;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting user: id={}", id);

        User user = userRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Delete user failed: not found id={}",
                            id
                    );
                    return new UserNotFoundException(id);
                });

        String email = user.getEmail();

        userRepository.delete(user);

        eventPublisher.publishEvent(
                new UserEvent(email, UserEvent.Operation.DELETE)
        );

        log.info("User deleted: id={}", id);
    }

    private User toEntity(UserCreateRequest request) {
        return new User(
                request.name(),
                request.email().toLowerCase(Locale.ROOT),
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
