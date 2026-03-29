package com.memorygrove.backend.messenger.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на создание или обновление шаблона сообщения
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание/обновление шаблона сообщения")
public class MessageTemplateRequestDto {

    /**
     * Название шаблона (для отображения на кнопке)
     */
    @NotBlank(message = "Название шаблона не может быть пустым")
    @Size(max = 100, message = "Название шаблона не должно превышать 100 символов")
    @Schema(
            description = "Название шаблона",
            example = "Приветствие",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    private String title;

    /**
     * Текст шаблона
     */
    @NotBlank(message = "Текст шаблона не может быть пустым")
    @Size(max = 500, message = "Текст шаблона не должен превышать 500 символов")
    @Schema(
            description = "Текст шаблона",
            example = "Привет! Как у тебя дела?",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("content")
    private String content;

    /**
     * Порядок сортировки
     */
    @Schema(
            description = "Порядок сортировки",
            example = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("sortOrder")
    private Integer sortOrder;
}
