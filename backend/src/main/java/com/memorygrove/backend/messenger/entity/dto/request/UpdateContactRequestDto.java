package com.memorygrove.backend.messenger.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на обновление настроек доверенного контакта
 * <p>
 * Позволяет изменить никнейм и настройки уведомлений для существующего контакта.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление настроек контакта")
public class UpdateContactRequestDto {

    /**
     * Новый никнейм для отображения
     */
    @Size(max = 100, message = "Длина никнейма не должна превышать 100 символов")
    @Schema(
            description = "Новый никнейм",
            example = "Любимый внук",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("nickname")
    private String nickname;

    /**
     * Флаг активности контакта
     * <p>
     * Можно временно заблокировать общение, не удаляя контакт
     * </p>
     */
    @Schema(
            description = "Активен ли контакт",
            example = "true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("isActive")
    private Boolean isActive;

    /**
     * Может ли контакт отправлять сообщения
     */
    @Schema(
            description = "Может ли контакт отправлять сообщения",
            example = "true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("canSendMessages")
    private Boolean canSendMessages;

    /**
     * Получать ли уведомления от этого контакта
     */
    @Schema(
            description = "Получать ли уведомления",
            example = "true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("canReceiveNotifications")
    private Boolean canReceiveNotifications;
}
