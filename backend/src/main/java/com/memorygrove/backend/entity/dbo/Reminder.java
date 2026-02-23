package com.memorygrove.backend.entity.dbo;

import com.memorygrove.backend.dictionary.ReminderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
 * Сущность "Напоминание"
 * <p>
 * Представляет собой напоминание для пользователя о различных событиях:
 * приём лекарств, визит к врачу, важные дела и т.д.
 * Каждое напоминание привязано к конкретному пользователю и имеет статус,
 * определяющий его текущее состояние в системе.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reminders")
public class Reminder {

    /**
     * Уникальный идентификатор напоминания
     * Генерируется автоматически при сохранении в базу данных
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, которому принадлежит напоминание
     * Связь многие-к-одному с сущностью User
     * Одно напоминание может принадлежать только одному пользователю
     */
    @NotNull(message = "Пользователь обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Заголовок напоминания
     * Краткое описание того, о чём нужно напомнить
     * Обязательное поле, максимальная длина 200 символов
     */
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 1, max = 200, message = "Заголовок должен содержать от 1 до 200 символов")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Развёрнутое описание напоминания
     * Может содержать дополнительные детали, инструкции или примечания
     * Опциональное поле, максимальная длина 500 символов
     */
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Column(name = "description")
    private String description;

    /**
     * Дата и время, когда должно сработать напоминание
     * Обязательное поле. Используется для определения момента отправки уведомления
     */
    @NotNull(message = "Время напоминания обязательно")
    @Column(name = "remind_at", nullable = false)
    private LocalDateTime remindAt;

    /**
     * Текущий статус напоминания
     * Связь многие-к-одному со справочником ReminderStatus
     * Определяет жизненный цикл напоминания: ожидание, отправлено, выполнено, отменено
     */
    @NotNull(message = "Статус обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ReminderStatus status;

    /**
     * Дата и время создания напоминания
     * Устанавливается автоматически при первом сохранении
     * Поле не может быть изменено после создания
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления напоминания
     * Обновляется автоматически при каждом изменении сущности
     * Может быть null, если напоминание никогда не обновлялось
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Проверяет, имеет ли напоминание статус PENDING (ожидает отправки)
     *
     * @return true если статус PENDING, иначе false
     */
    @Transient
    public boolean isPending() {
        return status != null && ReminderStatus.Constants.PENDING.equals(status.getId());
    }

    /**
     * Проверяет, имеет ли напоминание статус SENT (отправлено)
     *
     * @return true если статус SENT, иначе false
     */
    @Transient
    public boolean isSent() {
        return status != null && ReminderStatus.Constants.SENT.equals(status.getId());
    }

    /**
     * Проверяет, имеет ли напоминание статус COMPLETED (выполнено пользователем)
     *
     * @return true если статус COMPLETED, иначе false
     */
    @Transient
    public boolean isCompleted() {
        return status != null && ReminderStatus.Constants.COMPLETED.equals(status.getId());
    }

    /**
     * Проверяет, имеет ли напоминание статус CANCELLED (отменено)
     *
     * @return true если статус CANCELLED, иначе false
     */
    @Transient
    public boolean isCancelled() {
        return status != null && ReminderStatus.Constants.CANCELLED.equals(status.getId());
    }

    /**
     * Проверяет, является ли напоминание активным
     * Активным считается напоминание со статусом PENDING,
     * время срабатывания которого ещё не наступило
     *
     * @return true если напоминание активно, иначе false
     */
    @Transient
    public boolean isActive() {
        return isPending() && remindAt.isAfter(LocalDateTime.now());
    }

    /**
     * Проверяет, является ли напоминание просроченным
     * Просроченным считается напоминание со статусом PENDING,
     * время срабатывания которого уже прошло
     *
     * @return true если напоминание просрочено, иначе false
     */
    @Transient
    public boolean isOverdue() {
        return isPending() && remindAt.isBefore(LocalDateTime.now());
    }
}
