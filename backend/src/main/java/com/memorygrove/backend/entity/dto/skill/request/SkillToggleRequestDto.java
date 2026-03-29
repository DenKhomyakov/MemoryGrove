package com.memorygrove.backend.entity.dto.skill.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для быстрого переключения статуса навыка
 * <p>
 * Используется для отметки навыка как выполненного/невыполненного
 * через чек-бокс в интерфейсе, без необходимости отправлять все поля навыка.
 * Автоматически обновляет дату последнего выполнения при отметке "выполнено".
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на переключение статуса навыка")
public class SkillToggleRequestDto {

    /**
     * Новый статус выполнения навыка
     * <p>
     * true - пользователь может выполнить навык (отметка "умею")
     * false - пользователь пока не может выполнить навык
     * </p>
     */
    @NotNull(message = "Статус обязателен")
    @Schema(
            description = "Может ли выполнять сейчас",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("canDoNow")
    private Boolean canDoNow;

    /**
     * Дата и время последнего выполнения (опционально)
     * <p>
     * Если поле передано, оно будет использовано как дата последнего выполнения.
     * Если поле не передано, но canDoNow = true, дата установится автоматически
     * на текущий момент на сервере.
     * </p>
     */
    @Schema(
            description = "Дата последнего выполнения (опционально)",
            example = "2026-03-01T10:30:00",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("lastPerformedAt")
    private LocalDateTime lastPerformedAt;
}
