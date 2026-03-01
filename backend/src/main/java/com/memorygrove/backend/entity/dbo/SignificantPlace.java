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
 * Сущность "Значимое место"
 * <p>
 * Представляет собой место, важное для пользователя: дом, работа, поликлиника,
 * любимый парк, университет и т.д. Помогает пользователю ориентироваться
 * в пространстве и сохранять связь с важными локациями.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "significant_places")
public class SignificantPlace {

    /**
     * Уникальный идентификатор места
     * Генерируется автоматически при сохранении в базу данных
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, которому принадлежит место
     * Связь многие-к-одному с сущностью User
     */
    @NotNull(message = "Пользователь обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Название места
     * Краткое, запоминающееся название. Например: "Дом", "Поликлиника №1", "Любимый парк"
     */
    @NotBlank(message = "Название места не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Полный адрес места
     * Включает город, улицу, номер дома, корпус и т.д.
     */
    @NotBlank(message = "Адрес не может быть пустым")
    @Size(max = 250, message = "Адрес не должен превышать 250 символов")
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * Дополнительное описание или заметки о месте
     * Часы работы, контактные данные, номера кабинетов и т.д.
     */
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Column(name = "description")
    private String description;

    /**
     * Категория места
     * Для фильтрации и группировки: "Дом", "Медицина", "Работа", "Отдых", "Образование", "Другое"
     */
    @Column(name = "category")
    private String category;

    /**
     * Координаты места (широта)
     * Для будущей интеграции с картами
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * Координаты места (долгота)
     * Для будущей интеграции с картами
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * Флаг, является ли место домашним адресом
     */
    @Column(name = "is_home", nullable = false)
    private boolean isHome = false;

    /**
     * Флаг, является ли место работой или учёбой
     */
    @Column(name = "is_work", nullable = false)
    private boolean isWork = false;

    /**
     * Флаг, является ли место медицинским учреждением
     */
    @Column(name = "is_medical", nullable = false)
    private boolean isMedical = false;

    /**
     * Порядок сортировки
     * Определяет позицию места в списке. Меньшее число = выше в списке.
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * Дата и время создания записи
     * Устанавливается автоматически при первом сохранении
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего редактирования
     * Обновляется автоматически при каждом изменении
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
