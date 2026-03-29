package com.memorygrove.backend.entity.dto.skill.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для общей статистики по всем навыкам пользователя
 * <p>
 * Содержит суммарную информацию по всем категориям:
 * общее количество навыков, количество освоенных навыков
 * и общий процент освоения.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Общая статистика по навыкам")
public class TotalSkillStatsDto {

    /**
     * Общее количество навыков во всех категориях
     */
    @Schema(
            description = "Общее количество навыков",
            example = "20",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("total")
    private long total;

    /**
     * Общее количество освоенных навыков (canDoNow = true)
     */
    @Schema(
            description = "Количество освоенных навыков",
            example = "12",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("mastered")
    private long mastered;

    /**
     * Общий процент освоения всех навыков
     * <p>
     * Вычисляется как (mastered / total) * 100
     * </p>
     */
    @Schema(
            description = "Общий процент освоения",
            example = "60",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("percentage")
    private int percentage;
}
