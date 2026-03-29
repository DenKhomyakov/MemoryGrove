package com.memorygrove.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Класс для конфигурации JWT (JSON Web Token) параметров
 * Содержит настройки для генерации и валидации JWT токенов
 * Значения загружаются из application.properties с префиксом "jwt"
 *
 * @see com.memorygrove.backend.util.JwtUtil
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Секретный ключ для подписи JWT токенов
     * Должен быть достаточно длинным и сложным (минимум 256 бит)
     * Хранится в переменной окружения JWT_SECRET_KEY или в application.properties
     */
    private String secretKey;

    /**
     * Время жизни access токена в миллисекундах
     * По умолчанию 15 минут (900000 мс)
     * Можно переопределить через JWT_ACCESS_TOKEN_EXPIRATION_MS
     */
    private long accessTokenExpirationMs;

    /**
     * Время жизни refresh токена в миллисекундах
     * По умолчанию 7 дней (604800000 мс)
     * Можно переопределить через JWT_REFRESH_TOKEN_EXPIRATION_MS
     */
    private long refreshTokenExpirationMs;
}
