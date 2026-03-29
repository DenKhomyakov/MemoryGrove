package com.memorygrove.backend.entity.dto.journal.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для обновления существующей записи в дневнике
 * <p>
 * Содержит поля, которые можно изменить в существующей записи.
 * Поля user, createdAt и другие системные поля не могут быть изменены.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для обновления записи в дневнике")
public class JournalEntryUpdateRequestDto {

    /**
     * Новый заголовок записи
     * Обязательное поле, от 1 до 200 символов
     */
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 1, max = 200, message = "Заголовок должен содержать от 1 до 200 символов")
    @Schema(
            description = "Заголовок записи",
            example = "Обновлённые мысли",
            minLength = 1,
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    @Description("Заголовок записи")
    private String title;

    /**
     * Новое содержание записи
     * Обязательное поле
     */
    @NotBlank(message = "Содержание не может быть пустым")
    @Schema(
            description = "Содержание записи",
            example = "Сегодня я понял, что...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("content")
    @Description("Содержание записи")
    private String content;

    /**
     * Новое настроение
     * Опциональное поле
     */
    @Size(max = 50, message = "Длина отметки о настроении не должна превышать 50 символов")
    @Schema(
            description = "Настроение (опционально)",
            example = "😊 отличное",
            maxLength = 50,
            nullable = true
    )
    @JsonProperty("mood")
    @Description("Настроение (опционально)")
    private String mood;
}
