package com.memorygrove.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация для кодирования паролей
 * Определяет бин для шифрования паролей пользователей с использованием BCrypt
 *
 * @see org.springframework.security.crypto.password.PasswordEncoder
 */
@Configuration
public class PasswordEncoderConfiguration {

    /**
     * Создает и настраивает бин для кодирования паролей
     * Используется алгоритм BCrypt с силой 12 (количество этапов/раундов хеширования)
     * Чем выше значение, тем безопаснее, но медленнее
     *
     * @return настроенный PasswordEncoder для шифрования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
