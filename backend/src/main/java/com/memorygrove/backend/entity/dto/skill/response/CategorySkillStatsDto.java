package com.memorygrove.backend.entity.dto.skill.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для статистики по категории навыков
 * <p>
 * Содержит агрегированную информацию по всем навыкам пользователя
 * в рамках одной категории: общее количество, количество освоенных
 * и процент освоения.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Статистика по категории навыков")
public class CategorySkillStatsDto {

    /**
     * Кодовое название категории (английское)
     * <p>
     * Используется для идентификации категории в коде.
     * Например: "SELF_CARE", "HOUSEHOLD", "COMMUNICATION"
     * </p>
     */
    @Schema(
            description = "Кодовое название категории",
            example = "SELF_CARE",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("category")
    private String category;

    /**
     * Отображаемое название категории с эмодзи
     * <p>
     * Используется для отображения пользователю.
     * Например: "🚿 Самообслуживание", "🍳 Быт"
     * </p>
     */
    @Schema(
            description = "Отображаемое название категории",
            example = "🚿 Самообслуживание",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("displayName")
    private String displayName;

    /**
     * Общее количество навыков в категории
     */
    @Schema(
            description = "Общее количество навыков в категории",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("total")
    private long total;

    /**
     * Количество освоенных навыков (canDoNow = true)
     */
    @Schema(
            description = "Количество освоенных навыков",
            example = "3",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("mastered")
    private long mastered;

    /**
     * Процент освоения категории
     * <p>
     * Вычисляется как (mastered / total) * 100
     * </p>
     */
    @Schema(
            description = "Процент освоения",
            example = "60",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("percentage")
    private int percentage;
}
