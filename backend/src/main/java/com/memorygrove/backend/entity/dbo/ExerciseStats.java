package com.memorygrove.backend.entity.dbo;

import com.memorygrove.backend.dictionary.ExerciseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность "Статистика упражнений"
 * <p>
 * Хранит статистику выполнения упражнений для тренировки памяти.
 * Для каждого пользователя и каждого типа упражнения создаётся отдельная запись.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "exercise_stats")
public class ExerciseStats {

    /**
     * Уникальный идентификатор записи статистики
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, к которому относится статистика
     */
    @NotNull(message = "Пользователь обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Тип упражнения
     */
    @NotNull(message = "Тип упражнения обязателен")
    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false)
    private ExerciseType exerciseType;

    /**
     * Общее количество попыток выполнения упражнения
     */
    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    /**
     * Количество успешных попыток
     */
    @Column(name = "successes", nullable = false)
    private int successes = 0;

    /**
     * Лучший результат
     * <p>
     * Для арифметической цепочки - максимальная длина решённой цепочки.
     * Для других упражнений может быть максимальное количество очков.
     * </p>
     */
    @Column(name = "best_score")
    private Integer bestScore;

    /**
     * Лучшее время выполнения в секундах
     */
    @Column(name = "best_time_seconds")
    private Integer bestTimeSeconds;

    /**
     * Дата и время последней попытки
     */
    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    /**
     * Дата и время создания записи
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Вычисляет процент успешных попыток
     *
     * @return процент успеха (0-100)
     */
    @Transient
    public int getSuccessRate() {
        if (attempts == 0) {
            return 0;
        }
        return (successes * 100) / attempts;
    }

    /**
     * Обновляет статистику после выполнения упражнения
     *
     * @param isSuccess была ли попытка успешной
     * @param score полученный результат
     * @param timeSeconds затраченное время в секундах
     */
    public void updateStats(boolean isSuccess, Integer score, Integer timeSeconds) {
        this.attempts++;
        if (isSuccess) {
            this.successes++;
        }

        if (score != null && (this.bestScore == null || score > this.bestScore)) {
            this.bestScore = score;
        }

        if (timeSeconds != null && (this.bestTimeSeconds == null || timeSeconds < this.bestTimeSeconds)) {
            this.bestTimeSeconds = timeSeconds;
        }

        this.lastAttemptAt = LocalDateTime.now();
    }
}
