package ru.aston.homework.service;
import jakarta.validation.Valid;
import ru.aston.homework.dto.*;
import java.util.Optional;
import java.util.List;

public interface UserService {
    UserResponse create(@Valid UserCreateRequest request);
    UserResponse update(@Valid UserUpdateRequest request);
    Optional<UserResponse> findById(Long id);
    List<UserResponse> findAll();
    void delete(Long id);
}
