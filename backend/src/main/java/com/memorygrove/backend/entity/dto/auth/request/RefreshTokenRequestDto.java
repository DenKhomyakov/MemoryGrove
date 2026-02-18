package com.memorygrove.backend.entity.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для обновления JWT токена
 * Содержит refresh токен, который используется для получения новой пары токенов
 * после истечения срока действия access токена
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для обнолвения токена")
public class RefreshTokenRequestDto {

    /**
     * Refresh токен для получения новой пары токенов
     * Должен быть действительным и не истекшим
     * Поле обязательно для заполнения
     */
    @NotBlank(message = "Refresh токен обязателен")
    @Schema(description = "Токен обновления")
    private String refreshToken;
}
