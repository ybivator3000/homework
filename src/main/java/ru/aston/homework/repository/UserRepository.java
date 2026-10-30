package ru.aston.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.homework.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}