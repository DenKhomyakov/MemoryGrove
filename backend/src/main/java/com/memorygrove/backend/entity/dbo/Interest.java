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
 * Сущность "Интерес" (Коллекция интересов)
 * <p>
 * Представляет собой элемент из коллекции интересов пользователя:
 * фильмы, книги, музыка, игры, спорт, хобби и т.д.
 * Позволяет отслеживать, что нравится пользователю в данный момент,
 * и сравнивать с его прошлыми предпочтениями.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "interests")
public class Interest {

    /**
     * Уникальный идентификатор интереса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, которому принадлежит интерес
     */
    @NotNull(message = "Пользователь обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Категория интереса
     * <p>
     * Допустимые значения:
     * <ul>
     *   <li>FILM - Фильмы</li>
     *   <li>BOOK - Книги</li>
     *   <li>MUSIC - Музыка</li>
     *   <li>GAME - Игры</li>
     *   <li>SPORT - Спорт</li>
     *   <li>HOBBY - Хобби (конструкторы, пазлы, вышивание и т.д.)</li>
     *   <li>OTHER - Другое</li>
     * </ul>
     * </p>
     */
    @NotBlank(message = "Категория обязательна")
    @Column(name = "category", nullable = false)
    private String category;

    /**
     * Название интереса
     * <p>
     * Например: "Зелёная миля", "Властелин колец", "Бетховен", "Шахматы", "Футбол", "Пазлы"
     * </p>
     */
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 1, max = 200, message = "Название должно содержать от 1 до 200 символов")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Автор/исполнитель/создатель
     * <p>
     * Для фильмов: режиссёр, для книг: автор, для музыки: исполнитель и т.д.
     * </p>
     */
    @Size(max = 200, message = "Имя автора не должно превышать 200 символов")
    @Column(name = "creator")
    private String creator;

    /**
     * Год создания/выхода
     */
    @Column(name = "year")
    private Integer year;

    /**
     * Жанр/категория внутри основной категории
     * <p>
     * Например: "Фантастика", "Драма", "Рок", "Стратегия"
     * </p>
     */
    @Size(max = 100, message = "Жанр не должен превышать 100 символов")
    @Column(name = "genre")
    private String genre;

    /**
     * Описание или заметки об интересе
     */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Column(name = "description")
    private String description;

    /**
     * Оценка/статус "нравится"
     * <p>
     * true - нравится (лайк)
     * false - не нравится (дизлайк)
     * null - не определено
     * </p>
     */
    @Column(name = "liked")
    private Boolean liked;

    /**
     * Оценка по шкале (1-5)
     * <p>
     * Опционально, для более детальной оценки
     * </p>
     */
    @Column(name = "rating")
    private Integer rating;

    /**
     * Флаг, является ли текущим интересом
     * <p>
     * true - нравится сейчас
     * false - раньше нравилось, но сейчас нет
     * </p>
     */
    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    /**
     * Порядок сортировки
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
     * Проверяет, был ли интерес отредактирован
     *
     * @return true если запись редактировалась
     */
    @Transient
    public boolean isEdited() {
        return updatedAt != null && !updatedAt.equals(createdAt);
    }

    /**
     * Проверяет, установлена ли оценка "нравится"
     *
     * @return true если liked == true
     */
    @Transient
    public boolean isLiked() {
        return liked != null && liked;
    }

    /**
     * Проверяет, установлена ли оценка "не нравится"
     *
     * @return true если liked == false
     */
    @Transient
    public boolean isDisliked() {
        return liked != null && !liked;
    }
}
