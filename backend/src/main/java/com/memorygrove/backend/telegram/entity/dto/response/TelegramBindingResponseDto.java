package com.memorygrove.backend.telegram.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO для ответа API со статусом привязки Telegram
 * <p>
 * Содержит полную информацию о текущей привязке Telegram аккаунта:
 * статус привязки, данные пользователя Telegram, дату привязки,
 * статус уведомлений
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO со статусом привязки Telegram")
public class TelegramBindingResponseDto {

    /**
     * Флаг, указывающий, привязан ли Telegram аккаунт
     */
    @Schema(
            description = "Флаг привязки Telegram",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isBound", access = JsonProperty.Access.READ_ONLY)
    private boolean isBound;

    /**
     * Username пользователя в Telegram (без @)
     */
    @Schema(
            description = "Username в Telegram",
            example = "ivan_petrov",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "telegramUsername", access = JsonProperty.Access.READ_ONLY)
    private String telegramUsername;

    /**
     * Имя пользователя в Telegram
     */
    @Schema(
            description = "Имя в Telegram",
            example = "Иван",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "firstName", access = JsonProperty.Access.READ_ONLY)
    private String firstName;

    /**
     * Фамилия пользователя в Telegram
     */
    @Schema(
            description = "Фамилия в Telegram",
            example = "Петров",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "lastName", access = JsonProperty.Access.READ_ONLY)
    private String lastName;

    /**
     * Дата и время привязки аккаунта
     */
    @Schema(
            description = "Дата и время привязки",
            example = "2026-02-25T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "boundAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime boundAt;

    /**
     * Отформатированная дата привязки для отображения
     */
    @Schema(
            description = "Отформатированная дата привязки",
            example = "25 февраля 2026, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "formattedBoundAt", access = JsonProperty.Access.READ_ONLY)
    private String formattedBoundAt;

    /**
     * Флаг, включены ли уведомления в Telegram
     */
    @Schema(
            description = "Включены ли уведомления",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "notificationsEnabled", access = JsonProperty.Access.READ_ONLY)
    private boolean notificationsEnabled;

    /**
     * Конструктор для создания DTO из сущности
     *
     * @param isBound флаг привязки
     * @param telegramUsername username в Telegram
     * @param firstName имя в Telegram
     * @param lastName фамилия в Telegram
     * @param boundAt дата привязки
     * @param notificationsEnabled статус уведомлений
     */
    public TelegramBindingResponseDto(boolean isBound, String telegramUsername, String firstName,
                                      String lastName, LocalDateTime boundAt, boolean notificationsEnabled) {
        this.isBound = isBound;
        this.telegramUsername = telegramUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.boundAt = boundAt;
        this.notificationsEnabled = notificationsEnabled;

        if (boundAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");
            this.formattedBoundAt = boundAt.format(formatter);
        }
    }
}
