package com.memorygrove.backend.repository;

import com.memorygrove.backend.entity.dbo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с пользователем
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Поиск пользователя по электронной почте
     * @param email электронная почта пользователя
     * @return Optional с найденной электронной почтой, либо пустой Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверка существования пользователя по электронной почте
     * @param email электронная почта пользователя
     * @return true - существует, false - не существует
     */
    boolean existsByEmail(String email);

    /**
     * Поиск пользователя по номеру телефона
     * @param phone номер телефона пользователя
     * @return Optional с найденным номером телефона, либо пустой Optional
     */
    Optional<User> findByPhone(String phone);

    /**
     * Проверка существования пользователя по номеру телефона
     * @param phone номер телефона пользователя
     * @return true - существует, false - не существует
     */
    boolean existsByPhone(String phone);

    /**
     * Найти пользователя по электронной почте или номеру телефона
     * @param identifier идентификтаор поиска (электронная почта / номер телефона пользователя)
     * @return Optional с найденным идентификатором, либо пустой Optional
     */
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phone = :identifier")
    Optional<User> findByEmailOrPhone(@Param("identifier") String identifier);

    /**
     * Поиск активных пользователей
     */
    List<User> findByIsActiveTrue();

    /**
     * Поиск неактивных пользователей
     */
    List<User> findByIsActiveFalse();

    /**
     * Поиск пользователей по полу
     * @param sexId UUID пола
     * @return Список пользователей указанного пола
     */
    @Query("SELECT u FROM User u WHERE u.sex.id = :sexId")
    List<User> findBySexId(@Param("sexId") UUID sexId);

    /**
     * Поиск пользователей по роли
     * @param roleId UUID роли
     * @return Список пользователей указанной роли
     */
    @Query("SELECT u FROM User u WHERE u.role.id = :roleId")
    List<User> findByRoleId(@Param("roleId") UUID roleId);

    /**
     * Поиск пользователей, созданных жо указанной даты
     * @param date Дата
     * @return Список пользователей, созданных до указанной даты
     */
    List<User> findByCreatedAtBefore(LocalDateTime date);

    /**
     * Поиск пользователей, созданных после указанной даты
     * @param date Дата
     * @return Список пользователей, созданных после указанной даты
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Поиск пользователей, которые не входили в систему больше указанного количества дней
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date OR u.lastLoginAt IS NULL")
    List<User> findInactiveUserSince(@Param("date") LocalDateTime date);

    /**
     * Подсчёт количества активных пользователей
     */
    long countByIsActiveTrue();

    /**
     * Подсчёт количества неактивных пользователей
     */
    long countByIsActiveFalse();

    /**
     * Подсчёт количества пользователей по полу
     * @param sexId UUID пола
     * @return количество пользователей указанного пола
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.sex.id = :sexId")
    long countBySexId(@Param("sexId") UUID sexId);

    /**
     * Подсчёт количества пользователей по роли
     * @param roleId UUID роли
     * @return количество пользователей указанной роли
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId")
    long countByRoleId(@Param("roleId") UUID roleId);
}
