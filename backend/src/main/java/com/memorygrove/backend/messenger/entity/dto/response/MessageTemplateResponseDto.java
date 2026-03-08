package com.memorygrove.backend.messenger.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.messenger.entity.dbo.MessageTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO для ответа с информацией о шаблоне сообщения
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о шаблоне сообщения")
public class MessageTemplateResponseDto {

    /**
     * ID шаблона
     */
    @Schema(
            description = "ID шаблона",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("id")
    private UUID id;

    /**
     * Название шаблона
     */
    @Schema(
            description = "Название шаблона",
            example = "Приветствие",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("title")
    private String title;

    /**
     * Текст шаблона
     */
    @Schema(
            description = "Текст шаблона",
            example = "Привет! Как у тебя дела?",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("content")
    private String content;

    /**
     * Порядок сортировки
     */
    @Schema(
            description = "Порядок сортировки",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("sortOrder")
    private Integer sortOrder;

    /**
     * Флаг системного шаблона
     */
    @Schema(
            description = "Системный шаблон",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("isDefault")
    private boolean isDefault;

    /**
     * Дата создания
     */
    @Schema(
            description = "Дата создания",
            example = "2026-03-09T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    /**
     * Отформатированная дата создания
     */
    @Schema(
            description = "Отформатированная дата создания",
            example = "9 марта 2026, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("formattedCreatedAt")
    private String formattedCreatedAt;

    /**
     * Конвертирует сущность MessageTemplate в DTO
     *
     * @param template сущность шаблона
     * @return DTO с данными шаблона
     */
    public static MessageTemplateResponseDto fromEntity(MessageTemplate template) {
        MessageTemplateResponseDto dto = new MessageTemplateResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        dto.setId(template.getId());
        dto.setTitle(template.getTitle());
        dto.setContent(template.getContent());
        dto.setSortOrder(template.getSortOrder());
        dto.setDefault(template.isDefault());
        dto.setCreatedAt(template.getCreatedAt());

        if (template.getCreatedAt() != null) {
            dto.setFormattedCreatedAt(template.getCreatedAt().format(formatter));
        }

        return dto;
    }
}
