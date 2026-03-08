package com.memorygrove.backend.messenger.entity.dbo;

import com.memorygrove.backend.entity.dbo.User;
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
 * Сущность "Шаблон сообщения"
 * <p>
 * Представляет собой готовые фразы для быстрого ответа.
 * Шаблоны могут быть системными (для всех пользователей) и пользовательскими.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "message_templates")
public class MessageTemplate {

    /**
     * Уникальный идентификатор шаблона
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, создавший шаблон
     * <p>
     * Для системных шаблонов может быть null
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    /**
     * Название шаблона (для отображения на кнопке)
     * <p>
     * Например: "Приветствие", "Всё хорошо", "Нужна помощь"
     * </p>
     */
    @NotBlank(message = "Название шаблона не может быть пустым")
    @Size(max = 100, message = "Название шаблона не должно превышать 100 символов")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Текст шаблона
     */
    @NotBlank(message = "Текст шаблона не может быть пустым")
    @Size(max = 500, message = "Текст шаблона не должен превышать 500 символов")
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * Порядок сортировки
     * <p>
     * Чем меньше число, тем выше в списке
     * </p>
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * Флаг системного шаблона
     * <p>
     * Системные шаблоны нельзя удалить, они доступны всем пользователям
     * </p>
     */
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    /**
     * Дата и время создания шаблона
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления шаблона
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Проверяет, является ли шаблон системным
     *
     * @return true если системный
     */
    @Transient
    public boolean isSystemTemplate() {
        return isDefault || user == null;
    }
}
