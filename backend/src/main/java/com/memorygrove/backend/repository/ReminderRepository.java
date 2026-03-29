package com.memorygrove.backend.repository;

import com.memorygrove.backend.entity.dbo.Reminder;
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
 * Репозиторий для работы с напоминаниями пользователей.
 * <p>
 * Предоставляет методы для поиска, фильтрации и проверки напоминаний,
 * а также для получения статистики по ним.
 * </p>
 */
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    /**
     * Находит все напоминания пользователя с поддержкой пагинации и сортировкой по дате
     *
     * @param userId идентификатор пользователя
     * @param pageable параметры пагинации (страница, размер, сортировка)
     * @return страница с напоминаниями пользователя
     */
    Page<Reminder> findByUserIdOrderByRemindAtDesc(UUID userId, Pageable pageable);

    /**
     * Находит все напоминания пользователя без пагинации
     *
     * @param userId идентификатор пользователя
     * @return список всех напоминаний пользователя, отсортированных по дате
     */
    List<Reminder> findByUserIdOrderByRemindAtDesc(UUID userId);

    /**
     * Находит напоминания пользователя по конкретному статусу
     *
     * @param userId идентификатор пользователя
     * @param statusId идентификатор статуса напоминания
     * @return список напоминаний с указанным статусом
     */
    @Query("SELECT r FROM Reminder r WHERE r.user.id = :userId AND r.status.id = :statusId ORDER BY r.remindAt")
    List<Reminder> findByUserIdAndStatusId(@Param("userId") UUID userId, @Param("statusId") UUID statusId);

    /**
     * Находит все активные напоминания (со статусом PENDING), время срабатывания которых наступило
     * Используется планировщиком для отправки уведомлений
     *
     * @param pendingStatusId идентификатор статуса PENDING
     * @param now текущее время для сравнения
     * @return список напоминаний, готовых к отправке
     */
    @Query("SELECT r FROM Reminder r WHERE r.status.id = :pendingStatusId AND r.remindAt <= :now")
    List<Reminder> findDueReminders(@Param("pendingStatusId") UUID pendingStatusId,
                                    @Param("now") LocalDateTime now);

    /**
     * Находит напоминания пользователя за указанный период времени
     *
     * @param userId идентификатор пользователя
     * @param start начало периода
     * @param end конец периода
     * @return список напоминаний в заданном периоде
     */
    List<Reminder> findByUserIdAndRemindAtBetweenOrderByRemindAt(
            UUID userId, LocalDateTime start, LocalDateTime end);

    /**
     * Проверяет, принадлежит ли напоминание указанному пользователю
     * Используется для проверки прав доступа
     *
     * @param reminderId идентификатор напоминания
     * @param userId идентификатор пользователя
     * @return true если напоминание существует и принадлежит пользователю
     */
    @Query("SELECT COUNT(r) > 0 FROM Reminder r WHERE r.id = :reminderId AND r.user.id = :userId")
    boolean existsByIdAndUserId(@Param("reminderId") UUID reminderId, @Param("userId") UUID userId);

    /**
     * Удаляет все напоминания пользователя
     * Используется при деактивации или удалении аккаунта
     *
     * @param userId идентификатор пользователя
     */
    void deleteByUserId(UUID userId);

    /**
     * Подсчитывает количество напоминаний пользователя по статусу
     *
     * @param userId идентификатор пользователя
     * @param statusId идентификатор статуса
     * @return количество напоминаний с указанным статусом
     */
    long countByUserIdAndStatusId(UUID userId, UUID statusId);
}
