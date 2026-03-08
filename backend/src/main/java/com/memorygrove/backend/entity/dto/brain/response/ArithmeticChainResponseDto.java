package com.memorygrove.backend.entity.dto.brain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для ответа с арифметической цепочкой
 * <p>
 * Содержит выражение, которое нужно решить, и правильный ответ для проверки.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с арифметической цепочкой для решения")
public class ArithmeticChainResponseDto {

    /**
     * Строка с выражением для решения
     * <p>
     * Например: "5 + 3 - 2 + 7"
     * </p>
     */
    @Schema(
            description = "Выражение для решения",
            example = "5 + 3 - 2 + 7",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "expression", access = JsonProperty.Access.READ_ONLY)
    private String expression;

    /**
     * Правильный результат
     * <p>
     * Используется на фронтенде для проверки (можно не показывать пользователю)
     * </p>
     */
    @Schema(
            description = "Правильный результат (для проверки)",
            example = "13",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "correctResult", access = JsonProperty.Access.READ_ONLY)
    private int correctResult;

    /**
     * Длина цепочки (количество операций)
     */
    @Schema(
            description = "Длина цепочки (количество операций)",
            example = "3",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "chainLength", access = JsonProperty.Access.READ_ONLY)
    private int chainLength;
}
