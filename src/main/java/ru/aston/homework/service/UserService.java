package ru.aston.homework.service;
import ru.aston.homework.dto.*;
import java.util.Optional;
import java.util.List;

public interface UserService {
    UserResponse create(UserCreateRequest request);
    UserResponse update(UserUpdateRequest request);
    Optional<UserResponse> findById(Long id);
    List<UserResponse> findAll();
    void delete(Long id);
}
