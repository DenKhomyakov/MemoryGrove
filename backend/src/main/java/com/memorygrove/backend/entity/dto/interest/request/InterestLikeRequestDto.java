package com.memorygrove.backend.entity.dto.interest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для запроса на изменение оценки (лайк/дизлайк) интереса
 * <p>
 * Используется для быстрого обновления только отношения пользователя к интересу,
 * без необходимости отправлять все поля сущности.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на изменение оценки интереса")
public class InterestLikeRequestDto {

    /**
     * Отношение к интересу
     * <p>
     * true - пользователю нравится (лайк)
     * false - пользователю не нравится (дизлайк)
     * </p>
     */
    @NotNull(message = "Оценка обязательна")
    @Schema(
            description = "Нравится ли (true) или не нравится (false)",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("liked")
    private Boolean liked;

    /**
     * Оценка по шкале (опционально)
     * <p>
     * Дополнительная детализация отношения. Например:
     * 5 - шедевр, 4 - очень нравится, 3 - нормально, 2 - так себе, 1 - совсем не нравится
     * </p>
     */
    @Schema(
            description = "Оценка по шкале (1-5)",
            example = "5",
            minimum = "1",
            maximum = "5",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("rating")
    private Integer rating;
}
