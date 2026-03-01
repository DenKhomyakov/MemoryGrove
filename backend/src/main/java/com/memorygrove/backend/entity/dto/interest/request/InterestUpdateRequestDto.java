package com.memorygrove.backend.entity.dto.interest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для обновления существующего интереса
 * <p>
 * Содержит поля, которые можно изменить в уже созданном интересе,
 * включая возможность изменения порядка сортировки.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление интереса")
public class InterestUpdateRequestDto {

    /**
     * Категория интереса
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
     */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Schema(
            description = "Описание или заметки",
            example = "Очень трогательный фильм о дружбе и надежде.",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("description")
    private String description;

    /**
     * Отношение к интересу
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
     */
    @Schema(
            description = "Флаг текущего интереса (true - актуально сейчас)",
            example = "true"
    )
    @JsonProperty("isCurrent")
    private Boolean isCurrent;

    /**
     * Порядок сортировки
     * <p>
     * Числовое значение, определяющее позицию интереса в списке внутри категории.
     * Меньшее число = выше в списке.
     * </p>
     */
    @Schema(
            description = "Порядок сортировки (меньше = выше в списке)",
            example = "1",
            minimum = "1"
    )
    @JsonProperty("sortOrder")
    private Integer sortOrder;
}
