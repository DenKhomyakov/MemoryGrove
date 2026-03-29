package com.memorygrove.backend.entity.dbo;

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
 * Сущность "Запись в личном дневнике"
 * <p>
 * Представляет собой запись, созданную пользователем в модуле "Безопасный лес".
 * Каждая запись привязана к конкретному пользователю и содержит заголовок,
 * содержание, опциональную отметку о настроении и временные метки
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "journal_entries")
public class JournalEntry {

    /**
     * Уникальный идентификатор записи
     * Генерируется автоматически при сохранении
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, которому принадлежит запись
     * Связь многие-к-одному с сущностью User
     */
    @NotNull(message = "Пользователь обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Заголовок записи
     * Обязательное поле, максимальная длина 200 символов
     */
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 1, max = 200, message = "Заголовок должен содержать от 1 до 200 символов")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Содержание записи
     * Обязательное поле, может содержать большой объём текста
     */
    @NotBlank(message = "Содержание не может быть пустым")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Настроение пользователя в момент создания записи
     * Опциональное поле, максимальная длина 50 символов
     * Например: "😊 хорошее", "😔 грустно", "🤔 задумчивое"
     */
    @Size(max = 50, message = "Длина отметки о настроении не должна превышать 50 символов")
    @Column(name = "mood")
    private String mood;

    /**
     * Дата и время создания записи
     * Устанавливается автоматически при первом сохранении
     * Не может быть изменено после создания
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего редактирования записи
     * Обновляется автоматически при каждом изменении
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Проверяет, была ли запись отредактирована
     *
     * @return true если запись редактировалась (updatedAt не null и не равен createdAt)
     */
    @Transient
    public boolean isEdited() {
        return updatedAt != null && !updatedAt.equals(createdAt);
    }
}
