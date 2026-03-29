package com.memorygrove.backend.entity.dto.journal.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.entity.dbo.JournalEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO для ответа API с данными записи дневника
 * <p>
 * Содержит полную информацию о записи, включая временные метки
 * и вычисляемые поля для удобства отображения на фронтенде.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для ответа API с данными записи дневника")
public class JournalEntryResponseDto {

    /**
     * ID записи
     */
    @Schema(
            description = "Уникальный идентификатор записи",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    @Description("ID записи")
    private UUID id;

    /**
     * ID пользователя (владельца записи)
     */
    @Schema(
            description = "ID пользователя (владельца записи)",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "userId", access = JsonProperty.Access.READ_ONLY)
    @Description("ID пользователя (владельца записи)")
    private UUID userId;

    /**
     * Заголовок записи
     */
    @Schema(
            description = "Заголовок записи",
            example = "Мои мысли сегодня",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "title", access = JsonProperty.Access.READ_ONLY)
    @Description("Заголовок записи")
    private String title;

    /**
     * Содержание записи
     */
    @Schema(
            description = "Содержание записи",
            example = "Сегодня я вспомнил, как мы гуляли в парке...",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "content", access = JsonProperty.Access.READ_ONLY)
    @Description("Содержание записи")
    private String content;

    /**
     * Настроение
     */
    @Schema(
            description = "Настроение",
            example = "😊 хорошее",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "mood", access = JsonProperty.Access.READ_ONLY)
    @Description("Настроение")
    private String mood;

    /**
     * Дата и время создания записи
     */
    @Schema(
            description = "Дата и время создания записи",
            example = "2025-02-23T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Дата и время создания записи")
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего редактирования
     */
    @Schema(
            description = "Дата и время последнего редактирования",
            example = "2025-02-23T16:45:00",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "updatedAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Дата и время последнего редактирования")
    private LocalDateTime updatedAt;

    /**
     * Отформатированная дата создания для отображения
     */
    @Schema(
            description = "Отформатированная дата создания",
            example = "23 февраля 2025, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "formattedCreatedAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Отформатированная дата создания")
    private String formattedCreatedAt;

    /**
     * Отформатированная дата редактирования
     */
    @Schema(
            description = "Отформатированная дата редактирования",
            example = "23 февраля 2025, 16:45",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "formattedUpdatedAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Отформатированная дата редактирования")
    private String formattedUpdatedAt;

    /**
     * Флаг, была ли запись отредактирована
     */
    @Schema(
            description = "Флаг, была ли запись отредактирована",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isEdited", access = JsonProperty.Access.READ_ONLY)
    @Description("Флаг, была ли запись отредактирована")
    private boolean isEdited;

    /**
     * Конвертирует сущность JournalEntry в DTO для ответа
     *
     * @param entry сущность записи дневника
     * @return DTO с данными записи
     */
    public static JournalEntryResponseDto fromEntity(JournalEntry entry) {
        JournalEntryResponseDto dto = new JournalEntryResponseDto();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        dto.setId(entry.getId());
        dto.setUserId(entry.getUser().getId());
        dto.setTitle(entry.getTitle());
        dto.setContent(entry.getContent());
        dto.setMood(entry.getMood());
        dto.setCreatedAt(entry.getCreatedAt());
        dto.setUpdatedAt(entry.getUpdatedAt());
        dto.setFormattedCreatedAt(entry.getCreatedAt() != null ? entry.getCreatedAt().format(formatter) : null);
        dto.setFormattedUpdatedAt(entry.getUpdatedAt() != null ? entry.getUpdatedAt().format(formatter) : null);
        dto.setEdited(entry.isEdited());

        return dto;
    }
}
