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
 * DTO для создания новой записи в дневник
 * <p>
 * Содержит все необходимые поля для создания записи.
 * Поля user и временные метки устанавливаются автоматически на сервере.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания новой записи в дневнике")
public class JournalEntryCreateRequestDto {

    /**
     * Заголовок записи
     * Обязательное поле, от 1 до 200 символов
     */
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 1, max = 200, message = "Заголовок должен содержать от 1 до 200 символов")
    @Schema(
            description = "Заголовок записи",
            example = "Мои мысли сегодня",
            minLength = 1,
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    @Description("Заголовок записи")
    private String title;

    /**
     * Содержание записи
     * Обязательное поле, без ограничений по длине
     */
    @NotBlank(message = "Содержание не может быть пустым")
    @Schema(
            description = "Содержание записи",
            example = "Сегодня я вспомнил, как мы гуляли в парке...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("content")
    @Description("Содержание записи")
    private String content;

    /**
     * Настроение в момент записи
     * Опциональное поле
     */
    @Size(max = 50, message = "Длина отметки о настроении не должна превышать 50 символов")
    @Schema(
            description = "Настроение (опционально)",
            example = "😊 хорошее",
            maxLength = 50,
            nullable = true
    )
    @JsonProperty("mood")
    @Description("Настроение (опционально)")
    private String mood;
}
