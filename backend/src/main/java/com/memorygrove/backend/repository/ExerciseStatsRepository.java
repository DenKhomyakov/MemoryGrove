package com.memorygrove.backend.repository;

import com.memorygrove.backend.dictionary.ExerciseType;
import com.memorygrove.backend.entity.dbo.ExerciseStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы со статистикой упражнений
 */
@Repository
public interface ExerciseStatsRepository extends JpaRepository<ExerciseStats, UUID> {

    /**
     * Найти статистику пользователя по типу упражнения
     *
     * @param userId ID пользователя
     * @param exerciseType тип упражнения
     * @return Optional со статистикой
     */
    Optional<ExerciseStats> findByUserIdAndExerciseType(UUID userId, ExerciseType exerciseType);

    /**
     * Найти всю статистику пользователя
     *
     * @param userId ID пользователя
     * @return список статистики по всем упражнениям
     */
    List<ExerciseStats> findByUserIdOrderByLastAttemptAtDesc(UUID userId);

    /**
     * Проверить, существует ли статистика для пользователя по типу упражнения
     *
     * @param userId ID пользователя
     * @param exerciseType тип упражнения
     * @return true если существует
     */
    boolean existsByUserIdAndExerciseType(UUID userId, ExerciseType exerciseType);
}
