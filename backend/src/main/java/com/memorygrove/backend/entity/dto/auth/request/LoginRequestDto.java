package com.memorygrove.backend.entity.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для аутентификации пользователя
 * Содержит данные, необходимые для входа в систему
 * Валидация полей осуществляется с помощью аннотаций Jakarta Validation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для аутентификации пользователя")
public class LoginRequestDto {

    /**
     * Идентификатор пользователя для входа в систему
     * Может быть как электронной почтой, так и номером телефона
     * Поле обязательно для заполнения
     */
    @NotBlank(message = "Идентификатор (электронная почта или номер телефона) обязателен")
    @Schema(
            description = "Идентификатор пользователя (электронная почта или номер телефона)",
            example = "LunevGA@yandex.ru или +79830000000"
    )
    private String identifier;

    /**
     * Пароль пользователя
     * Должен содержать от 8 до 50 символов
     * Поле обязательно для заполнения
     */
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 50, message = "Длина пароля должна быть от 8 до 50 символов")
    @Schema(
            description = "Пароль пользователя",
            example = "aN43R_V7CuJO",
            minLength = 8,
            maxLength = 50
    )
    private String password;
}
