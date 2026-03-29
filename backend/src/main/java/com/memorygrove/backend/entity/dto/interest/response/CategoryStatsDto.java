package com.memorygrove.backend.entity.dto.interest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для статистики по категории интересов
 * <p>
 * Содержит агрегированную информацию по всем интересам пользователя
 * в рамках одной категории. Используется для отображения на дашборде
 * или для аналитики.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Статистика по категории интересов")
public class CategoryStatsDto {

    /**
     * Кодовое название категории (английское)
     * <p>
     * Используется для идентификации категории в коде.
     * Например: "FILM", "BOOK", "MUSIC"
     * </p>
     */
    @Schema(
            description = "Кодовое название категории",
            example = "FILM",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("category")
    private String category;

    /**
     * Отображаемое название категории (русское)
     * <p>
     * Используется для отображения пользователю.
     * Например: "Фильмы", "Книги", "Музыка"
     * </p>
     */
    @Schema(
            description = "Отображаемое название категории",
            example = "Фильмы",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("displayName")
    private String displayName;

    /**
     * Общее количество элементов в категории
     */
    @Schema(
            description = "Количество элементов в категории",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("count")
    private long count;

    /**
     * Количество лайков в категории
     * <p>
     * Сколько элементов в этой категории пользователь отметил как "нравится"
     * </p>
     */
    @Schema(
            description = "Количество лайков в категории",
            example = "3",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("likesCount")
    private long likesCount;

    /**
     * Получить отображаемое название категории по кодовому названию
     *
     * @param category кодовое название категории
     * @return отображаемое название на русском
     */
    public static String getDisplayName(String category) {
        switch (category) {
            case "FILM": return "Фильмы";
            case "BOOK": return "Книги";
            case "MUSIC": return "Музыка";
            case "GAME": return "Игры";
            case "SPORT": return "Спорт";
            case "HOBBY": return "Хобби";
            case "OTHER": return "Другое";
            default: return category;
        }
    }
}
