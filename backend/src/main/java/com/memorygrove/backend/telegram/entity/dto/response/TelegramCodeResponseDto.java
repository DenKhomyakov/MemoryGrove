package com.memorygrove.backend.telegram.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа API с кодом привязки Telegram
 * <p>
 * Содержит сгенерированный код, который пользователь должен отправить
 * Telegram боту для привязки аккаунта.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO с кодом для привязки Telegram")
public class TelegramCodeResponseDto {

    /**
     * Уникальный код для привязки Telegram аккаунта
     * Состоит из 6 символов (цифры и заглавные буквы)
     * Действителен в течение 10 минут
     */
    @Schema(
            description = "Код для привязки Telegram (6 символов)",
            example = "A1B2C3",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "code", access = JsonProperty.Access.READ_ONLY)
    private String code;
}
