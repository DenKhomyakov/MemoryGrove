package com.memorygrove.backend.messenger.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на отправку сообщения
 * <p>
 * Содержит ID получателя и текст сообщения.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на отправку сообщения")
public class SendMessageRequestDto {

    /**
     * ID получателя сообщения
     */
    @NotBlank(message = "ID получателя обязателен")
    @Schema(
            description = "ID пользователя, которому отправляем сообщение",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("receiverId")
    private String receiverId;

    /**
     * Текст сообщения
     */
    @NotBlank(message = "Текст сообщения не может быть пустым")
    @Size(max = 2000, message = "Длина сообщения не должна превышать 2000 символов")
    @Schema(
            description = "Текст сообщения",
            example = "Привет! Как твои дела?",
            maxLength = 2000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("content")
    private String content;
}
