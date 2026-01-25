package com.memorygrove.backend.entity.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для смены пароля пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для смены пароля пользователя")
public class UserPasswordChangeDto {

    /**
     * Текущий пароль пользователя
     */
    @NotBlank(message = "Поле 'Текущий пароль' обязательно")
    @Size(min = 8, max = 50, message = "Длина пароля должна быть от 8 до 50 символов")
    @Schema(
            description = "Текущий пароль пользователя",
            example = "aN43R_V7CuJO",
            minLength = 8,
            maxLength = 50,
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    @JsonProperty(value = "currentPassword", access = JsonProperty.Access.WRITE_ONLY)
    @Description("Текущий пароль пользователя")
    private String currentPassword;

    /**
     * Новый пароль пользователя
     */
    @NotBlank(message = "Поле 'Новый пароль' обязательно")
    @Size(min = 8, max = 50, message = "Длина пароля должна быть от 8 до 50 символов")
    @Schema(
            description = "Новый пароль пользователя",
            example = "5IxZyoW2b-S5",
            minLength = 8,
            maxLength = 50,
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    @JsonProperty(value = "newPassword", access = JsonProperty.Access.WRITE_ONLY)
    @Description("Новый пароль пользователя")
    private String newPassword;

    /**
     * Подтверждение нового пароля пользователя
     * Поле должно совпадать с полем {@link #newPassword}
     */
    @NotBlank(message = "Поле 'Подтверждение нового пароля' обязательно")
    @Size(min = 8, max = 50, message = "Длина подтвержденного пароля должна быть от 8 до 50 символов")
    @Schema(
            description = "Подтверждение нового пароля пользователя",
            example = "5IxZyoW2b-S5",
            minLength = 8,
            maxLength = 50,
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    @JsonProperty(value = "confirmNewPassword", access = JsonProperty.Access.WRITE_ONLY)
    @Description("Подтверждение нового пароля пользователя")
    private String confirmNewPassword;
}
