package com.memorygrove.backend.repository;

import com.memorygrove.backend.entity.dbo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с пользователем
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Поиск пользователя по электронной почте
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверка существования пользователя по электронной почте
     */
    boolean existsByEmail(String email);

    /**
     * Поиск пользователя по номеру телефона
     */
    Optional<User> findByPhone(String phone);

    /**
     * Проверка существования пользователя по номеру телефона
     */
    boolean existsByPhone(String phone);

    /**
     * Найти пользователя по электронной почте или номеру телефона
     */
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phone = :identifier")
    Optional<User> findByEmailOrPhone(@Param("identifier") String identifier);
}
