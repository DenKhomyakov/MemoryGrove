package com.memorygrove.backend.entity.dto.reminder.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO для запроса на изменение статуса напоминания
 * <p>
 * Упрощённая версия для операций, где нужно изменить только статус
 * (отметка о выполнении, отмена и т.д.)
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на изменение статуса напоминания")
public class ReminderStatusUpdateRequestDto {

    /**
     * Новый статус напоминания
     */
    @NotNull(message = "Статус обязателен")
    @Schema(
            description = "ID статуса напоминания",
            example = "7665777b-e1e9-4be7-a8c5-fb2e7d457b20",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("statusId")
    @Description("ID статуса напоминания")
    private UUID statusId;
}
