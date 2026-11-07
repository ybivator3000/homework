package ru.aston.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.homework.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
}