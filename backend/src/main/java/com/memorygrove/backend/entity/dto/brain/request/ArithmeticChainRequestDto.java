package com.memorygrove.backend.entity.dto.brain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для отправки ответа на арифметическую цепочку
 * <p>
 * Содержит результат, который пользователь ввёл, и метаданные о попытке.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос с ответом на арифметическую цепочку")
public class ArithmeticChainRequestDto {

    /**
     * Результат, который ввёл пользователь
     */
    @NotNull(message = "Результат обязателен")
    @Schema(
            description = "Результат вычислений, введённый пользователем",
            example = "12",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("result")
    private Integer result;

    /**
     * Время, затраченное на решение (в секундах)
     */
    @Schema(
            description = "Время решения в секундах",
            example = "45",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("timeSeconds")
    private Integer timeSeconds;

    /**
     * Длина решённой цепочки (количество операций)
     */
    @Schema(
            description = "Длина цепочки (количество операций)",
            example = "3",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("chainLength")
    private Integer chainLength;
}
