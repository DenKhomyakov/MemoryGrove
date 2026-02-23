package com.memorygrove.backend.repository;

import com.memorygrove.backend.entity.dbo.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с записями дневника
 * <p>
 * Предоставляет методы для поиска и фильтрации записей,
 * а также для статистики по записям пользователя.
 * </p>
 */
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {

    /**
     * Найти все записи пользователя с пагинацией
     *
     * @param userId ID пользователя
     * @param pageable параметры пагинации
     * @return страница с записями
     */
    Page<JournalEntry> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * Найти все записи пользователя
     *
     * @param userId ID пользователя
     * @return список записей
     */
    List<JournalEntry> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Найти запись по ID и проверить принадлежность пользователю
     *
     * @param id ID записи
     * @param userId ID пользователя
     * @return запись, если она существует и принадлежит пользователю
     */
    @Query("SELECT j FROM JournalEntry j WHERE j.id = :id AND j.user.id = :userId")
    JournalEntry findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    /**
     * Проверить, принадлежит ли запись пользователю
     *
     * @param id ID записи
     * @param userId ID пользователя
     * @return true если запись существует и принадлежит пользователю
     */
    @Query("SELECT COUNT(j) > 0 FROM JournalEntry j WHERE j.id = :id AND j.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    /**
     * Найти записи пользователя за определенный период
     *
     * @param userId ID пользователя
     * @param startDate начало периода
     * @param endDate конец периода
     * @return список записей
     */
    List<JournalEntry> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Найти записи пользователя по наличию настроения
     *
     * @param userId ID пользователя
     * @param hasMood true - только с настроением, false - только без настроения
     * @return список записей
     */
    @Query("SELECT j FROM JournalEntry j WHERE j.user.id = :userId AND " +
            "(:hasMood = true AND j.mood IS NOT NULL OR :hasMood = false AND j.mood IS NULL)")
    List<JournalEntry> findByUserIdAndMoodPresence(@Param("userId") UUID userId,
                                                   @Param("hasMood") boolean hasMood);

    /**
     * Получить статистику по записям пользователя
     *
     * @param userId ID пользователя
     * @return список с массивом объектов [общее количество, количество с настроением, количество отредактированных]
     */
    @Query("SELECT COUNT(j), " +
            "SUM(CASE WHEN j.mood IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN j.updatedAt IS NOT NULL AND j.updatedAt != j.createdAt THEN 1 ELSE 0 END) " +
            "FROM JournalEntry j WHERE j.user.id = :userId")
    List<Object[]> getJournalStats(@Param("userId") UUID userId);

    /**
     * Удалить все записи пользователя
     *
     * @param userId ID пользователя
     */
    void deleteByUserId(UUID userId);
}
