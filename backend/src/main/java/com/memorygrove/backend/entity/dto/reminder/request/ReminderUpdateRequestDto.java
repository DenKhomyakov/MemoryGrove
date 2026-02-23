package com.memorygrove.backend.entity.dto.reminder.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для запроса на обновление существующего напоминания
 * <p>
 * Содержит поля, которые можно изменить в существующем напоминании
 * Поля user, status, createdAt и другие системные поля не могут быть изменены
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление напоминания")
public class ReminderUpdateRequestDto {

    /**
     * Новый заголовок напоминания
     */
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 1, max = 200, message = "Заголовок должен содержать от 1 до 200 символов")
    @Schema(
            description = "Заголовок напоминания",
            example = "Принять лекарство (обновлено)",
            minLength = 1,
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    @Description("Заголовок напоминания")
    private String title;

    /**
     * Новое описание напоминания
     */
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Schema(
            description = "Описание напоминания (детали, инструкции)",
            example = "Препарат 'Мемантин', 20 мг, после завтрака (дозировка изменена)",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("description")
    @Description("Описание напоминания")
    private String description;

    /**
     * Новая дата и время срабатывания напоминания
     */
    @NotNull(message = "Время напоминания обязательно")
    @Schema(
            description = "Дата и время срабатывания напоминания",
            example = "2026-02-24T10:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("remindAt")
    @Description("Дата и время срабатывания напоминания")
    private LocalDateTime remindAt;

    /**
     * Новый статус напоминания
     * Позволяет изменить состояние напоминания (выполнено, отменено и т.д.)
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
