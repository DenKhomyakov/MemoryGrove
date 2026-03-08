package com.memorygrove.backend.messenger.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.messenger.entity.dbo.TrustedContact;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO для ответа с информацией о доверенном контакте
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о доверенном контакте")
public class ContactResponseDto {

    /**
     * ID записи о контакте
     */
    @Schema(
            description = "ID записи о контакте",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("id")
    private UUID id;

    /**
     * ID контакта (пользователя)
     */
    @Schema(
            description = "ID контакта",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("contactId")
    private UUID contactId;

    /**
     * ФИО контакта
     */
    @Schema(
            description = "ФИО контакта",
            example = "Петров Пётр Петрович",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("fullName")
    private String fullName;

    /**
     * Отображаемое имя (никнейм или ФИО)
     */
    @Schema(
            description = "Отображаемое имя",
            example = "Сын Петя",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("displayName")
    private String displayName;

    /**
     * Никнейм, заданный пользователем
     */
    @Schema(
            description = "Никнейм",
            example = "Сын Петя",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty("nickname")
    private String nickname;

    /**
     * Номер телефона контакта
     */
    @Schema(
            description = "Номер телефона",
            example = "+79830000000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("phone")
    private String phone;

    /**
     * Email контакта
     */
    @Schema(
            description = "Email",
            example = "petrov@example.com",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("email")
    private String email;

    /**
     * Дата добавления контакта
     */
    @Schema(
            description = "Дата добавления",
            example = "2026-03-09T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("addedAt")
    private LocalDateTime addedAt;

    /**
     * Отформатированная дата добавления
     */
    @Schema(
            description = "Отформатированная дата добавления",
            example = "9 марта 2026, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("formattedAddedAt")
    private String formattedAddedAt;

    /**
     * Активен ли контакт
     */
    @Schema(
            description = "Активен ли контакт",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("isActive")
    private boolean isActive;

    /**
     * Может ли контакт отправлять сообщения
     */
    @Schema(
            description = "Может ли отправлять сообщения",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("canSendMessages")
    private boolean canSendMessages;

    /**
     * Получать ли уведомления
     */
    @Schema(
            description = "Получать ли уведомления",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("canReceiveNotifications")
    private boolean canReceiveNotifications;

    /**
     * Конвертирует сущность TrustedContact в DTO
     *
     * @param contact сущность доверенного контакта
     * @return DTO с данными контакта
     */
    public static ContactResponseDto fromEntity(TrustedContact contact) {
        ContactResponseDto dto = new ContactResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        User contactUser = contact.getContact();

        dto.setId(contact.getId());
        dto.setContactId(contactUser.getId());
        dto.setFullName(contactUser.getFullName());
        dto.setDisplayName(contact.getDisplayName());
        dto.setNickname(contact.getNickname());
        dto.setPhone(contactUser.getPhone());
        dto.setEmail(contactUser.getEmail());
        dto.setAddedAt(contact.getAddedAt());
        dto.setActive(contact.isActive());
        dto.setCanSendMessages(contact.isCanSendMessages());
        dto.setCanReceiveNotifications(contact.isCanReceiveNotifications());

        if (contact.getAddedAt() != null) {
            dto.setFormattedAddedAt(contact.getAddedAt().format(formatter));
        }

        return dto;
    }
}
