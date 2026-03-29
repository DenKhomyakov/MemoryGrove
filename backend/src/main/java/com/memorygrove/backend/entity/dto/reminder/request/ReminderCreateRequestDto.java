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

/**
 * DTO для запроса на создание нового напоминания
 * <p>
 * Содержит все необходимые поля для создания напоминания
 * Поля user, status и временные метки устанавливаются автоматически на сервере
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание нового напоминания")
public class ReminderCreateRequestDto {

    /**
     * Заголовок напоминания
     * Краткое описание того, о чём нужно напомнить
     */
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 1, max = 200, message = "Заголовок должен содержать от 1 до 200 символов")
    @Schema(
            description = "Заголовок напоминания",
            example = "Принять лекарство",
            minLength = 1,
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    @Description("Заголовок напоминания")
    private String title;

    /**
     * Развёрнутое описание напоминания
     * Может содержать дополнительные детали, инструкции или примечания
     */
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Schema(
            description = "Описание напоминания (детали, инструкции)",
            example = "Препарат 'Мемантин', 20 мг, после завтрака",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("description")
    @Description("Описание напоминания")
    private String description;

    /**
     * Дата и время срабатывания напоминания
     * Определяет, когда система должна отправить уведомление пользователю
     */
    @NotNull(message = "Время напоминания обязательно")
    @Schema(
            description = "Дата и время срабатывания напоминания",
            example = "2026-02-24T09:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("remindAt")
    @Description("Дата и время срабатывания напоминания")
    private LocalDateTime remindAt;
}
