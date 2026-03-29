package com.memorygrove.backend.entity.dto.skill.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.entity.dbo.Skill;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для ответа API с данными о навыке
 * <p>
 * Содержит полную информацию о навыке пользователя, включая все поля сущности,
 * отформатированные даты для удобного отображения на фронтенде,
 * а также вычисляемое поле isEdited.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными о навыке")
public class SkillResponseDto {

    /**
     * Уникальный идентификатор навыка в системе
     */
    @Schema(
            description = "Уникальный идентификатор навыка",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    /**
     * Идентификатор пользователя, которому принадлежит навык
     */
    @Schema(
            description = "ID пользователя",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "userId", access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    /**
     * Кодовое название категории навыка
     */
    @Schema(
            description = "Категория навыка",
            example = "SELF_CARE",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "category", access = JsonProperty.Access.READ_ONLY)
    private String category;

    /**
     * Отображаемое название категории с эмодзи
     */
    @Schema(
            description = "Отображаемое название категории",
            example = "🚿 Самообслуживание",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "categoryDisplayName", access = JsonProperty.Access.READ_ONLY)
    private String categoryDisplayName;

    /**
     * Название навыка
     */
    @Schema(
            description = "Название навыка",
            example = "Чистить зубы",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "title", access = JsonProperty.Access.READ_ONLY)
    private String title;

    /**
     * Описание навыка
     */
    @Schema(
            description = "Описание навыка",
            example = "Почистить зубы щёткой с пастой, утром и вечером",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "description", access = JsonProperty.Access.READ_ONLY)
    private String description;

    /**
     * Флаг, может ли пользователь выполнять навык сейчас
     */
    @Schema(
            description = "Может ли выполнять сейчас",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "canDoNow", access = JsonProperty.Access.READ_ONLY)
    private boolean canDoNow;

    /**
     * Дата и время последнего успешного выполнения
     */
    @Schema(
            description = "Дата последнего выполнения",
            example = "2026-03-01T10:30:00",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "lastPerformedAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastPerformedAt;

    /**
     * Отформатированная дата последнего выполнения для отображения на фронтенде
     */
    @Schema(
            description = "Отформатированная дата последнего выполнения",
            example = "1 марта 2026, 10:30",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "formattedLastPerformedAt", access = JsonProperty.Access.READ_ONLY)
    private String formattedLastPerformedAt;

    /**
     * Сложность навыка (1-5)
     */
    @Schema(
            description = "Сложность (1-5)",
            example = "2",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "difficulty", access = JsonProperty.Access.READ_ONLY)
    private Integer difficulty;

    /**
     * Важность навыка (1-5)
     */
    @Schema(
            description = "Важность (1-5)",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "importance", access = JsonProperty.Access.READ_ONLY)
    private Integer importance;

    /**
     * Заметки или наблюдения
     */
    @Schema(
            description = "Заметки",
            example = "Нужна помощь с выдавливанием пасты",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "notes", access = JsonProperty.Access.READ_ONLY)
    private String notes;

    /**
     * Порядок сортировки внутри категории
     */
    @Schema(
            description = "Порядок сортировки",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "sortOrder", access = JsonProperty.Access.READ_ONLY)
    private Integer sortOrder;

    /**
     * Дата и время создания записи
     */
    @Schema(
            description = "Дата создания",
            example = "2026-03-01T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего редактирования
     */
    @Schema(
            description = "Дата последнего редактирования",
            example = "2026-03-01T16:45:00",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "updatedAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    /**
     * Отформатированная дата создания для отображения на фронтенде
     */
    @Schema(
            description = "Отформатированная дата создания",
            example = "1 марта 2026, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "formattedCreatedAt", access = JsonProperty.Access.READ_ONLY)
    private String formattedCreatedAt;

    /**
     * Отформатированная дата последнего редактирования
     */
    @Schema(
            description = "Отформатированная дата редактирования",
            example = "1 марта 2026, 16:45",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "formattedUpdatedAt", access = JsonProperty.Access.READ_ONLY)
    private String formattedUpdatedAt;

    /**
     * Флаг, был ли навык отредактирован после создания
     */
    @Schema(
            description = "Флаг редактирования",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isEdited", access = JsonProperty.Access.READ_ONLY)
    private boolean isEdited;

    /**
     * Получить отображаемое название категории по кодовому названию
     *
     * @param category кодовое название категории
     * @return отображаемое название с эмодзи
     */
    public static String getCategoryDisplayName(String category) {
        switch (category) {
            case "SELF_CARE": return "🚿 Самообслуживание";
            case "HOUSEHOLD": return "🍳 Быт";
            case "COMMUNICATION": return "📞 Коммуникация";
            case "MOBILITY": return "🚶 Мобильность";
            case "HEALTH": return "💊 Здоровье";
            case "COGNITIVE": return "🧠 Когнитивные";
            default: return "📌 Другое";
        }
    }

    /**
     * Конвертирует сущность Skill в DTO для ответа
     *
     * @param skill сущность навыка
     * @return DTO с данными навыка
     */
    public static SkillResponseDto fromEntity(Skill skill) {
        SkillResponseDto dto = new SkillResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        dto.setId(skill.getId());
        dto.setUserId(skill.getUser().getId());
        dto.setCategory(skill.getCategory());
        dto.setCategoryDisplayName(getCategoryDisplayName(skill.getCategory()));
        dto.setTitle(skill.getTitle());
        dto.setDescription(skill.getDescription());
        dto.setCanDoNow(skill.isCanDoNow());
        dto.setLastPerformedAt(skill.getLastPerformedAt());
        dto.setDifficulty(skill.getDifficulty());
        dto.setImportance(skill.getImportance());
        dto.setNotes(skill.getNotes());
        dto.setSortOrder(skill.getSortOrder());
        dto.setCreatedAt(skill.getCreatedAt());
        dto.setUpdatedAt(skill.getUpdatedAt());

        if (skill.getLastPerformedAt() != null) {
            dto.setFormattedLastPerformedAt(skill.getLastPerformedAt().format(formatter));
        }

        if (skill.getCreatedAt() != null) {
            dto.setFormattedCreatedAt(skill.getCreatedAt().format(formatter));
        }

        if (skill.getUpdatedAt() != null) {
            dto.setFormattedUpdatedAt(skill.getUpdatedAt().format(formatter));
        }

        dto.setEdited(skill.isEdited());

        return dto;
    }
}
