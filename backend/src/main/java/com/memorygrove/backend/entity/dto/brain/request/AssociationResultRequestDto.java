package com.memorygrove.backend.entity.dto.brain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для отправки результатов ассоциаций
 * <p>
 * Содержит информацию о том, как пользователь справился с упражнением.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос с результатами ассоциаций")
public class AssociationResultRequestDto {

    /**
     * Количество правильных ответов
     */
    @NotNull(message = "Количество правильных ответов обязательно")
    @Schema(
            description = "Количество правильных ответов",
            example = "8",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("correct")
    private Integer correct;

    /**
     * Общее количество вопросов
     */
    @NotNull(message = "Общее количество вопросов обязательно")
    @Schema(
            description = "Общее количество вопросов",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("total")
    private Integer total;

    /**
     * Процент правильных ответов
     */
    @NotNull(message = "Процент правильных ответов обязателен")
    @Schema(
            description = "Процент правильных ответов",
            example = "80",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("percentage")
    private Integer percentage;

    /**
     * Время выполнения в секундах
     */
    @Schema(
            description = "Время выполнения в секундах",
            example = "120",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("timeSeconds")
    private Integer timeSeconds;
}
