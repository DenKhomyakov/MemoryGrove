package com.memorygrove.backend.entity.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для ответа с JWT токенами
 * Содержит пару токенов (access и refresh), которые клиент должен сохранить
 * и использовать для аутентификации последующих запросов
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO ответа с JWT токенами (получаем при аутентификации)")
public class JwtResponseDto {

    /**
     * Токен доступа (access token)
     * Используется для аутентификации запросов к защищенным эндпоинтам
     * Имеет короткий срок жизни (по умолчанию 15 минут)
     */
    @Schema(description = "Токен доступа")
    private String accessToken;

    /**
     * Токен обновления (refresh token)
     * Используется для получения новой пары токенов после истечения access token
     * Имеет длинный срок жизни (по умолчанию 7 дней)
     */
    @Schema(description = "Токен обновления")
    private String refreshToken;

    /**
     * Тип токена
     * Всегда "Bearer" для соответствия стандарту JWT
     * Используется в заголовке Authorization: Bearer {token}
     */
    @Schema(description = "Тип токена")
    private String tokenType = "Bearer";
}
