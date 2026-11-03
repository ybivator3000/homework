package ru.aston.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.entity.User;
import ru.aston.homework.exception.EmailAlreadyTakenException;
import ru.aston.homework.exception.UserNotFoundException;
import ru.aston.homework.repository.UserRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void create_validRequest_returnsResponse() {
        var request = new UserCreateRequest("Ivan", "Ivan@Example.COM", 25);
        var savedUser = user(1L, "Ivan", "ivan@example.com", 25);

        when(userRepository.findByEmailIgnoreCase("Ivan@Example.COM")).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        UserResponse response = service.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Ivan");
        assertThat(response.email()).isEqualTo("ivan@example.com");
        assertThat(response.age()).isEqualTo(25);

        verify(userRepository).findByEmailIgnoreCase("Ivan@Example.COM");

        ArgumentCaptor<User> captured = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captured.capture());
        assertThat(captured.getValue().getEmail()).isEqualTo("ivan@example.com");
    }

    @Test
    void create_emailTaken_throwsServiceException() {
        var request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
        when(userRepository.findByEmailIgnoreCase("ivan@example.com"))
                .thenReturn(Optional.of(user(2L, "Other", "ivan@example.com", 30)));

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(EmailAlreadyTakenException.class)
                .hasMessageContaining("Электронная почта ivan@example.com уже занята");

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void create_repositoryThrows_propagates() {
        var request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
        when(userRepository.findByEmailIgnoreCase("ivan@example.com")).thenReturn(Optional.empty());

        when(userRepository.saveAndFlush(any(User.class))).thenThrow(new DataAccessResourceFailureException("DB down"));

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(DataAccessResourceFailureException.class);
    }

    @Test
    void update_validRequest_returnsResponse() {
        var request = new UserUpdateRequest(1L, "Ivan Updated", "ivan@example.com", 30);
        var existing = user(1L, "Ivan", "ivan@example.com", 25);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        UserResponse response = service.update(request);

        assertThat(response.name()).isEqualTo("Ivan Updated");
        assertThat(response.age()).isEqualTo(30);

        verify(userRepository).saveAndFlush(existing);
    }

    @Test
    void update_emailChangedToTakenByOther_throwsServiceException() {
        var request = new UserUpdateRequest(1L, "Ivan", "taken@example.com", 25);
        var existing = user(1L, "Ivan", "ivan@example.com", 25);
        var other = user(2L, "Other", "taken@example.com", 30);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        when(userRepository.findByEmailIgnoreCase("taken@example.com")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> service.update(request)).isInstanceOf(EmailAlreadyTakenException.class)
                .hasMessageContaining("Электронная почта taken@example.com уже занята");
    }

    @Test
    void update_sameEmail_noDuplicateCheck() {
        var request = new UserUpdateRequest(1L, "Ivan Updated", "IVAN@EXAMPLE.COM", 30);
        var existing = user(1L, "Ivan", "ivan@example.com", 25);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatNoException().isThrownBy(() -> service.update(request));

        verify(userRepository, never()).findByEmailIgnoreCase(any(String.class));

        verify(userRepository).saveAndFlush(existing);

        assertThat(existing.getEmail()).isEqualTo("ivan@example.com");
    }

    @Test
    void update_notFound_throwsServiceException() {
        var request = new UserUpdateRequest(99L, "Ivan", "ivan@example.com", 25);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(request)).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с ID 99 не найден");

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void findById_found_returnsResponse() {
        var existing = user(1L, "Ivan", "a@b.com", 25);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        Optional<UserResponse> result = service.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Ivan");
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserResponse> result = service.findById(99L);

        assertThat(result).isEmpty();

        verify(userRepository).findById(99L);
    }

    @Test
    void findAll_returnsAllUsers() {
        List<User> users = List.of(
                user(1L, "A", "a@b.com", 20),
                user(2L, "B", "b@b.com", 30)
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(UserResponse::name)
                .containsExactly("A", "B");

        verify(userRepository).findAll();
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = service.findAll();

        assertThat(result).isEmpty();

        verify(userRepository).findAll();
    }

    @Test
    void delete_existingUser_deletesUser() {
        var existing = user(1L, "Ivan", "a@b.com", 25);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(existing);
    }

    @Test
    void delete_notFound_throwsServiceException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с ID 99 не найден");

        verify(userRepository).findById(99L);
        verify(userRepository, never()).delete(any(User.class));
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