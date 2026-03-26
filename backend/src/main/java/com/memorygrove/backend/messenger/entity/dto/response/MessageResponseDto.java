package com.memorygrove.backend.messenger.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.messenger.entity.dbo.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO для ответа с информацией о сообщении
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о сообщении")
public class MessageResponseDto {

    @Schema(
            description = "ID сообщения",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("id")
    private UUID id;

    @Schema(
            description = "ID отправителя",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("senderId")
    private UUID senderId;

    @Schema(
            description = "Имя отправителя",
            example = "Иванов Иван",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("senderName")
    private String senderName;

    @Schema(
            description = "ID получателя",
            example = "93ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("receiverId")
    private UUID receiverId;

    @Schema(
            description = "Имя получателя",
            example = "Петров Пётр",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("receiverName")
    private String receiverName;

    @Schema(
            description = "Текст сообщения",
            example = "Привет! Как дела?",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("content")
    private String content;

    @Schema(
            description = "Дата и время отправки",
            example = "2026-03-09T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("sentAt")
    private LocalDateTime sentAt;

    @Schema(
            description = "Отформатированная дата отправки",
            example = "9 марта 2026, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("formattedSentAt")
    private String formattedSentAt;

    @Schema(
            description = "Дата и время прочтения",
            example = "2026-03-09T15:31:00",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty("readAt")
    private LocalDateTime readAt;

    @Schema(
            description = "Флаг, прочитано ли сообщение",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("isRead")
    private boolean isRead;

    @Schema(
            description = "Флаг, отправлено ли сообщение от текущего пользователя",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("isOutgoing")
    private boolean isOutgoing;

    @Schema(
            description = "URL фотографии профиля отправителя",
            example = "/api/users/83ba18af-2af1-4950-8a9e-6906a04b5461/photo",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty("senderPhotoUrl")
    private String senderPhotoUrl;

    public static MessageResponseDto fromEntity(Message message, UUID currentUserId) {
        MessageResponseDto dto = new MessageResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        User sender = message.getSender();
        User receiver = message.getReceiver();

        dto.setId(message.getId());
        dto.setSenderId(sender.getId());
        dto.setSenderName(sender.getFullName());
        dto.setReceiverId(receiver.getId());
        dto.setReceiverName(receiver.getFullName());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setReadAt(message.getReadAt());
        dto.setRead(message.isRead());
        dto.setOutgoing(sender.getId().equals(currentUserId));
        dto.setSenderPhotoUrl(sender.getPhotoUrl());

        if (message.getSentAt() != null) {
            dto.setFormattedSentAt(message.getSentAt().format(formatter));
        }

        return dto;
    }
}
