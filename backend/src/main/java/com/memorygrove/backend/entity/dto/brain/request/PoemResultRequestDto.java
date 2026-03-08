package com.memorygrove.backend.entity.dto.brain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для отправки результатов заучивания стиха
 * <p>
 * Содержит информацию о том, как пользователь справился с упражнением.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос с результатами заучивания стиха")
public class PoemResultRequestDto {

    /**
     * Флаг успешности (1 - успешно, 0 - неуспешно)
     */
    @NotNull(message = "Флаг успешности обязателен")
    @Schema(
            description = "Флаг успешности (1 - успешно, 0 - неуспешно)",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("correct")
    private Integer correct;

    /**
     * Процент схожести с оригиналом
     */
    @NotNull(message = "Процент схожести обязателен")
    @Schema(
            description = "Процент схожести с оригиналом",
            example = "85",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("similarity")
    private Integer similarity;

    /**
     * Общее время выполнения в секундах
     */
    @Schema(
            description = "Общее время выполнения в секундах",
            example = "120",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("timeSeconds")
    private Integer timeSeconds;

    /**
     * Время на заучивание в секундах
     */
    @Schema(
            description = "Время на заучивание в секундах",
            example = "45",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("memorizingTime")
    private Integer memorizingTime;
}
