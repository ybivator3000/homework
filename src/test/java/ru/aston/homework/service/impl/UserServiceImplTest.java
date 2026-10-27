package ru.aston.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.hibernate.Session;

import ru.aston.homework.dao.user.UserDao;
import ru.aston.homework.config.TransactionTemplate;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.entity.User;
import ru.aston.homework.exception.DaoException;
import ru.aston.homework.exception.ServiceException;
import ru.aston.homework.util.UserValidator;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserDao userDao;
    @Mock private UserValidator validator;
    @Mock private TransactionTemplate txTemplate;
    @Mock private Session session;

    @InjectMocks private UserServiceImpl service;

    @BeforeEach
    void stubTransactionTemplate() {
        lenient().when(txTemplate.execute(any())).thenAnswer(inv -> {
            Function<Session, ?> fn = inv.getArgument(0);
            return fn.apply(session);
        });
        lenient().when(txTemplate.executeReadOnly(any())).thenAnswer(inv -> {
            Function<Session, ?> fn = inv.getArgument(0);
            return fn.apply(session);
        });
        lenient().doAnswer(inv -> {
            Consumer<Session> fn = inv.getArgument(0);
            fn.accept(session);
            return null;
        }).when(txTemplate).executeVoid(any());
    }

    @Test
    void create_validRequest_returnsResponse() {
        var request = new UserCreateRequest("Ivan", "Ivan@Example.COM", 25);
        var savedUser = user(1L, "Ivan", "ivan@example.com", 25);

        when(userDao.findByEmail(session, "ivan@example.com")).thenReturn(Optional.empty());
        when(userDao.create(eq(session), any(User.class))).thenReturn(savedUser);

        UserResponse response = service.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Ivan");
        assertThat(response.email()).isEqualTo("ivan@example.com");
        assertThat(response.age()).isEqualTo(25);

        verify(validator).validate(request);
        verify(userDao).findByEmail(session, "ivan@example.com");

        ArgumentCaptor<User> captured = ArgumentCaptor.forClass(User.class);
        verify(userDao).create(eq(session), captured.capture());
        assertThat(captured.getValue().getEmail()).isEqualTo("ivan@example.com");
    }

    @Test
    void create_emailTaken_throwsServiceException() {
        var request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
        when(userDao.findByEmail(session, "ivan@example.com"))
                .thenReturn(Optional.of(user(2L, "Other", "ivan@example.com", 30)));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Email already taken")
                .hasMessageContaining("ivan@example.com");

        verify(userDao, never()).create(any(), any());
    }

    @Test
    void create_validatorThrows_propagatesAndSkipsDao() {
        var request = new UserCreateRequest("", "ivan@example.com", 25);
        doThrow(new ServiceException("Name must not be blank"))
                .when(validator).validate(request);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Name must not be blank");
        verifyNoInteractions(txTemplate, userDao);
    }

    @Test
    void create_daoThrows_propagates() {
        var request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
        when(userDao.findByEmail(session, "ivan@example.com")).thenReturn(Optional.empty());
        when(userDao.create(any(), any())).thenThrow(new DaoException("DB down"));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DaoException.class)
                .hasMessageContaining("DB down");
    }

    @Test
    void update_validRequest_returnsResponse() {
        var request = new UserUpdateRequest(1L, "Ivan Updated", "ivan@example.com", 30);
        var existing = user(1L, "Ivan", "ivan@example.com", 25);

        when(userDao.getById(session, 1L)).thenReturn(Optional.of(existing));
        when(userDao.update(eq(session), any(User.class))).thenAnswer(inv -> inv.getArgument(1));

        UserResponse response = service.update(request);

        assertThat(response.name()).isEqualTo("Ivan Updated");
        assertThat(response.age()).isEqualTo(30);
    }

    @Test
    void update_emailChangedToTakenByOther_throwsServiceException() {
        var request = new UserUpdateRequest(1L, "Ivan", "taken@example.com", 25);
        var existing = user(1L, "Ivan", "ivan@example.com", 25);
        var other = user(2L, "Other", "taken@example.com", 30);

        when(userDao.getById(session, 1L)).thenReturn(Optional.of(existing));
        when(userDao.findByEmail(session, "taken@example.com")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> service.update(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Email already taken");
    }

    @Test
    void update_sameEmail_noDuplicateCheck() {
        var request = new UserUpdateRequest(1L, "Ivan Updated", "IVAN@EXAMPLE.COM", 30);
        var existing = user(1L, "Ivan", "ivan@example.com", 25);

        when(userDao.getById(session, 1L)).thenReturn(Optional.of(existing));
        when(userDao.update(eq(session), any(User.class))).thenAnswer(inv -> inv.getArgument(1));

        assertThatNoException().isThrownBy(() -> service.update(request));

        verify(userDao, never()).findByEmail(any(), any());
    }

    @Test
    void update_notFound_throwsServiceException() {
        var request = new UserUpdateRequest(99L, "Ivan", "ivan@example.com", 25);
        when(userDao.getById(session, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining("99");

        verify(userDao, never()).update(any(), any());
    }

    @Test
    void findById_found_returnsResponse() {
        when(userDao.getById(session, 1L))
                .thenReturn(Optional.of(user(1L, "Ivan", "a@b.com", 25)));

        Optional<UserResponse> result = service.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Ivan");
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(userDao.getById(session, 99L)).thenReturn(Optional.empty());

        assertThat(service.findById(99L)).isEmpty();
    }

    @Test
    void findAll_returnsAllUsers() {
        when(userDao.getAll(session)).thenReturn(List.of(
                user(1L, "A", "a@b.com", 20),
                user(2L, "B", "b@b.com", 30)
        ));

        List<UserResponse> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserResponse::name).containsExactly("A", "B");
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(userDao.getAll(session)).thenReturn(List.of());

        assertThat(service.findAll()).isEmpty();
    }
    @Test
    void delete_existingUser_deletesUser() {
        var existing = user(1L, "Ivan", "a@b.com", 25);
        when(userDao.getById(session, 1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(userDao).delete(session, existing);
    }

    @Test
    void delete_notFound_throwsServiceException() {
        when(userDao.getById(session, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("User not found");

        verify(userDao, never()).delete(any(), any());
    }

    private static User user(Long id, String name, String email, int age) {
        User user = new User(name, email, age);
        setField(user, "id", id);
        setField(user, "createdAt", LocalDateTime.now());
        return user;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot set field " + fieldName, e);
        }
    }
}