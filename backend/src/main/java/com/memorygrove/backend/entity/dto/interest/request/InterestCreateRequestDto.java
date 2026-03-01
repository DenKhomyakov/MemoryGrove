package com.memorygrove.backend.entity.dto.interest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для создания нового интереса
 * <p>
 * Содержит все необходимые поля для добавления нового элемента в коллекцию
 * интересов пользователя. Включает категорию, название, информацию об авторе,
 * жанр, описание и возможность сразу указать отношение (лайк/дизлайк).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание нового интереса")
public class InterestCreateRequestDto {

    /**
     * Категория интереса
     * <p>
     * Определяет тип интереса. Допустимые значения:
     * <ul>
     *   <li>FILM - Фильмы</li>
     *   <li>BOOK - Книги</li>
     *   <li>MUSIC - Музыка</li>
     *   <li>GAME - Игры</li>
     *   <li>SPORT - Спорт</li>
     *   <li>HOBBY - Хобби (конструкторы, пазлы, вышивание)</li>
     *   <li>OTHER - Другое</li>
     * </ul>
     * </p>
     */
    @NotBlank(message = "Категория обязательна")
    @Schema(
            description = "Категория интереса",
            example = "FILM",
            allowableValues = {"FILM", "BOOK", "MUSIC", "GAME", "SPORT", "HOBBY", "OTHER"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("category")
    private String category;

    /**
     * Название интереса
     * <p>
     * Например: "Зелёная миля", "Властелин колец", "Симфония №5", "Шахматы"
     * </p>
     */
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 1, max = 200, message = "Название должно содержать от 1 до 200 символов")
    @Schema(
            description = "Название интереса",
            example = "Зелёная миля",
            minLength = 1,
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    private String title;

    /**
     * Автор/исполнитель/создатель
     * <p>
     * Для фильмов: режиссёр, для книг: автор, для музыки: исполнитель или композитор,
     * для игр: разработчик, для спорта: название команды или спортсмен
     * </p>
     */
    @Size(max = 200, message = "Имя автора не должно превышать 200 символов")
    @Schema(
            description = "Автор/исполнитель/создатель",
            example = "Фрэнк Дарабонт",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("creator")
    private String creator;

    /**
     * Год создания/выхода
     * <p>
     * Год, когда было создано произведение или когда состоялось значимое событие
     * </p>
     */
    @Schema(
            description = "Год создания/выхода",
            example = "1999",
            minimum = "1800",
            maximum = "2100",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("year")
    private Integer year;

    /**
     * Жанр
     * <p>
     * Уточняющая категория внутри основной. Например: "Фантастика", "Драма", "Рок"
     * </p>
     */
    @Size(max = 100, message = "Жанр не должен превышать 100 символов")
    @Schema(
            description = "Жанр произведения",
            example = "Драма",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("genre")
    private String genre;

    /**
     * Описание или заметки
     * <p>
     * Произвольный текст с дополнительной информацией, впечатлениями,
     * почему это важно для пользователя
     * </p>
     */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Schema(
            description = "Описание или заметки",
            example = "Очень трогательный фильм о дружбе и надежде. Пересматривал много раз.",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("description")
    private String description;

    /**
     * Отношение к интересу
     * <p>
     * true - нравится (лайк)
     * false - не нравится (дизлайк)
     * null - отношение не указано
     * </p>
     */
    @Schema(
            description = "Нравится ли сейчас (true - лайк, false - дизлайк, null - не указано)",
            example = "true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("liked")
    private Boolean liked;

    /**
     * Оценка по шкале
     * <p>
     * Оценка от 1 до 5 для более детального выражения отношения
     * </p>
     */
    @Schema(
            description = "Оценка (1-5)",
            example = "5",
            minimum = "1",
            maximum = "5",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("rating")
    private Integer rating;

    /**
     * Флаг текущего интереса
     * <p>
     * true - нравится в данный момент
     * false - раньше нравилось, но сейчас не актуально
     * </p>
     */
    @Schema(
            description = "Флаг текущего интереса (true - актуально сейчас)",
            example = "true",
            defaultValue = "true"
    )
    @JsonProperty("isCurrent")
    private boolean isCurrent = true;
}
