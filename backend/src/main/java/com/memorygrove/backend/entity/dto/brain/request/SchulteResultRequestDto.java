package com.memorygrove.backend.entity.dto.brain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для отправки результатов таблицы Шульте
 * <p>
 * Содержит информацию о том, как пользователь справился с упражнением.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос с результатами таблицы Шульте")
public class SchulteResultRequestDto {

    /**
     * Размер таблицы (3, 4, 5, 6, 7)
     */
    @NotNull(message = "Размер таблицы обязателен")
    @Schema(
            description = "Размер таблицы",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("size")
    private Integer size;

    /**
     * Время выполнения в секундах
     */
    @NotNull(message = "Время выполнения обязательно")
    @Schema(
            description = "Время выполнения в секундах",
            example = "45",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("timeSeconds")
    private Integer timeSeconds;

    /**
     * Флаг успешности (true - успешно)
     */
    @NotNull(message = "Флаг успешности обязателен")
    @Schema(
            description = "Флаг успешности",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("isSuccess")
    private Boolean isSuccess;
}
