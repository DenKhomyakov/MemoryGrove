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
 * Сущность "Навык" (Анкета навыков)
 * <p>
 * Представляет собой действие, которое пользователь умел выполнять ранее,
 * с возможностью отслеживать, может ли он выполнять его сейчас.
 * Помогает оценить прогресс реабилитации и выявить утраченные навыки.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "skills")
public class Skill {

    /**
     * Уникальный идентификатор навыка
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, которому принадлежит навык
     */
    @NotNull(message = "Пользователь обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Категория навыка
     * <p>
     * Допустимые значения:
     * <ul>
     *   <li>SELF_CARE - Самообслуживание (чистить зубы, одеваться и т.д.)</li>
     *   <li>HOUSEHOLD - Быт (готовка, уборка)</li>
     *   <li>COMMUNICATION - Коммуникация (звонки, сообщения)</li>
     *   <li>MOBILITY - Мобильность (прогулки, транспорт)</li>
     *   <li>HEALTH - Здоровье (лекарства, измерения)</li>
     *   <li>COGNITIVE - Когнитивные (чтение, счёт)</li>
     *   <li>OTHER - Другое</li>
     * </ul>
     * </p>
     */
    @NotBlank(message = "Категория обязательна")
    @Column(name = "category", nullable = false)
    private String category;

    /**
     * Название навыка
     * <p>
     * Например: "Чистить зубы", "Приготовить чай", "Позвонить родственникам"
     * </p>
     */
    @NotBlank(message = "Название навыка не может быть пустым")
    @Size(min = 3, max = 200, message = "Название должно содержать от 3 до 200 символов")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Описание навыка
     * <p>
     * Подробное описание того, что именно нужно делать, возможно с инструкцией
     * </p>
     */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Column(name = "description")
    private String description;

    /**
     * Флаг, умеет ли пользователь выполнять навык сейчас
     */
    @Column(name = "can_do_now", nullable = false)
    private boolean canDoNow = false;

    /**
     * Дата последнего успешного выполнения
     */
    @Column(name = "last_performed_at")
    private LocalDateTime lastPerformedAt;

    /**
     * Сложность навыка (1-5)
     * <p>
     * Оценка сложности выполнения: 1 - очень легко, 5 - очень сложно
     * </p>
     */
    @Column(name = "difficulty")
    private Integer difficulty;

    /**
     * Важность навыка (1-5)
     * <p>
     * Насколько важно сохранить этот навык для самостоятельной жизни
     * </p>
     */
    @Column(name = "importance")
    private Integer importance;

    /**
     * Заметки или наблюдения
     */
    @Size(max = 500, message = "Заметки не должны превышать 500 символов")
    @Column(name = "notes")
    private String notes;

    /**
     * Порядок сортировки внутри категории
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * Дата и время создания записи
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего редактирования
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Проверяет, был ли навык отредактирован
     *
     * @return true если запись редактировалась
     */
    @Transient
    public boolean isEdited() {
        return updatedAt != null && !updatedAt.equals(createdAt);
    }
}
