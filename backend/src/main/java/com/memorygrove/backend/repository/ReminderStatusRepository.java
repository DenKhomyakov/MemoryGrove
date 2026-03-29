package com.memorygrove.backend.repository;

import com.memorygrove.backend.dictionary.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы со справочником статусов напоминаний
 */
@Repository
public interface ReminderStatusRepository extends JpaRepository<ReminderStatus, UUID> {

    /**
     * Поиск статуса по наименованию
     */
    Optional<ReminderStatus> findByName(String name);

    /**
     * Проверка существования статуса по наименованию
     */
    boolean existsByName(String name);
}
