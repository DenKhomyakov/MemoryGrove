package com.memorygrove.backend.entity.dto.reminder.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.dictionary.ReminderStatus;
import com.memorygrove.backend.entity.dbo.Reminder;
import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO для ответа API с данными напоминания
 * <p>
 * Содержит полную информацию о напоминании, включая временные метки,
 * статус и вычисляемые поля для удобства отображения на фронтенде
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными напоминания")
public class ReminderResponseDto {

    /**
     * Уникальный идентификатор напоминания
     */
    @Schema(
            description = "Уникальный идентификатор напоминания",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    @Description("ID напоминания")
    private UUID id;

    /**
     * Идентификатор пользователя, которому принадлежит напоминание
     */
    @Schema(
            description = "ID пользователя (владельца напоминания)",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "userId", access = JsonProperty.Access.READ_ONLY)
    @Description("ID пользователя (владельца напоминания)")
    private UUID userId;

    /**
     * Заголовок напоминания
     */
    @Schema(
            description = "Заголовок напоминания",
            example = "Принять лекарство",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "title", access = JsonProperty.Access.READ_ONLY)
    @Description("Заголовок напоминания")
    private String title;

    /**
     * Развёрнутое описание напоминания
     */
    @Schema(
            description = "Описание напоминания",
            example = "Препарат 'Мемантин', 20 мг, после завтрака",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "description", access = JsonProperty.Access.READ_ONLY)
    @Description("Описание напоминания")
    private String description;

    /**
     * Дата и время срабатывания напоминания
     */
    @Schema(
            description = "Дата и время срабатывания напоминания",
            example = "2026-02-24T09:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "remindAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Дата и время срабатывания напоминания")
    private LocalDateTime remindAt;

    /**
     * Отформатированная дата и время срабатывания для отображения
     */
    @Schema(
            description = "Отформатированная дата и время срабатывания",
            example = "24 февраля 2026, 09:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "formattedRemindAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Отформатированная дата и время срабатывания")
    private String formattedRemindAt;

    /**
     * Статус напоминания (сущность)
     */
    @Schema(
            description = "Статус напоминания",
            implementation = ReminderStatus.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "status", access = JsonProperty.Access.READ_ONLY)
    @Description("Статус напоминания")
    private ReminderStatus status;

    /**
     * Дата и время создания напоминания
     */
    @Schema(
            description = "Дата и время создания напоминания",
            example = "2026-02-23T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Дата и время создания напоминания")
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего редактирования напоминания
     */
    @Schema(
            description = "Дата и время последнего редактирования",
            example = "2026-02-23T16:45:00",
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
            example = "23 февраля 2026, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "formattedCreatedAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Отформатированная дата создания")
    private String formattedCreatedAt;

    /**
     * Флаг активности напоминания (статус PENDING и время не наступило)
     */
    @Schema(
            description = "Флаг активности напоминания",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isActive", access = JsonProperty.Access.READ_ONLY)
    @Description("Флаг активности напоминания")
    private boolean isActive;

    /**
     * Флаг просроченности напоминания (статус PENDING, но время прошло)
     */
    @Schema(
            description = "Флаг просроченности напоминания",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isOverdue", access = JsonProperty.Access.READ_ONLY)
    @Description("Флаг просроченности напоминания")
    private boolean isOverdue;

    /**
     * Конвертирует сущность Reminder в DTO для ответа
     *
     * @param reminder сущность напоминания
     * @return DTO с данными напоминания
     */
    public static ReminderResponseDto fromEntity(Reminder reminder) {
        ReminderResponseDto dto = new ReminderResponseDto();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        dto.setId(reminder.getId());
        dto.setUserId(reminder.getUser().getId());
        dto.setTitle(reminder.getTitle());
        dto.setDescription(reminder.getDescription());
        dto.setRemindAt(reminder.getRemindAt());
        dto.setFormattedRemindAt(reminder.getRemindAt() != null ? reminder.getRemindAt().format(formatter) : null);
        dto.setStatus(reminder.getStatus());
        dto.setCreatedAt(reminder.getCreatedAt());
        dto.setUpdatedAt(reminder.getUpdatedAt());
        dto.setFormattedCreatedAt(reminder.getCreatedAt() != null ? reminder.getCreatedAt().format(formatter) : null);
        dto.setActive(reminder.isActive());
        dto.setOverdue(reminder.isOverdue());

        return dto;
    }
}
