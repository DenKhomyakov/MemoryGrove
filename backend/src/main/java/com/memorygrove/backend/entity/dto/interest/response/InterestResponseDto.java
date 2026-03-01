package com.memorygrove.backend.entity.dto.interest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.entity.dbo.Interest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для ответа API с данными об интересе
 * <p>
 * Содержит полную информацию об интересе пользователя, включая все поля сущности,
 * а также вычисляемые поля для удобства отображения на фронтенде.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными об интересе")
public class InterestResponseDto {

    /**
     * Уникальный идентификатор интереса
     */
    @Schema(
            description = "Уникальный идентификатор интереса",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    /**
     * Идентификатор пользователя, которому принадлежит интерес
     */
    @Schema(
            description = "ID пользователя",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "userId", access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    /**
     * Категория интереса
     */
    @Schema(
            description = "Категория интереса",
            example = "FILM",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "category", access = JsonProperty.Access.READ_ONLY)
    private String category;

    /**
     * Название интереса
     */
    @Schema(
            description = "Название интереса",
            example = "Зелёная миля",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "title", access = JsonProperty.Access.READ_ONLY)
    private String title;

    /**
     * Автор/исполнитель/создатель
     */
    @Schema(
            description = "Автор/исполнитель/создатель",
            example = "Фрэнк Дарабонт",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "creator", access = JsonProperty.Access.READ_ONLY)
    private String creator;

    /**
     * Год создания/выхода
     */
    @Schema(
            description = "Год создания/выхода",
            example = "1999",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "year", access = JsonProperty.Access.READ_ONLY)
    private Integer year;

    /**
     * Жанр
     */
    @Schema(
            description = "Жанр",
            example = "Драма",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "genre", access = JsonProperty.Access.READ_ONLY)
    private String genre;

    /**
     * Описание или заметки
     */
    @Schema(
            description = "Описание или заметки",
            example = "Очень трогательный фильм о дружбе и надежде.",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "description", access = JsonProperty.Access.READ_ONLY)
    private String description;

    /**
     * Отношение к интересу
     */
    @Schema(
            description = "Нравится ли (true) или не нравится (false)",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "liked", access = JsonProperty.Access.READ_ONLY)
    private Boolean liked;

    /**
     * Оценка по шкале
     */
    @Schema(
            description = "Оценка (1-5)",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "rating", access = JsonProperty.Access.READ_ONLY)
    private Integer rating;

    /**
     * Флаг текущего интереса
     */
    @Schema(
            description = "Флаг текущего интереса (true - актуально сейчас)",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isCurrent", access = JsonProperty.Access.READ_ONLY)
    private boolean isCurrent;

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
     * Отформатированная дата редактирования для отображения на фронтенде
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
     * Флаг, был ли интерес отредактирован
     */
    @Schema(
            description = "Флаг редактирования",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isEdited", access = JsonProperty.Access.READ_ONLY)
    private boolean isEdited;

    /**
     * Конвертирует сущность Interest в DTO для ответа
     *
     * @param interest сущность интереса
     * @return DTO с данными интереса
     */
    public static InterestResponseDto fromEntity(Interest interest) {
        InterestResponseDto dto = new InterestResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        dto.setId(interest.getId());
        dto.setUserId(interest.getUser().getId());
        dto.setCategory(interest.getCategory());
        dto.setTitle(interest.getTitle());
        dto.setCreator(interest.getCreator());
        dto.setYear(interest.getYear());
        dto.setGenre(interest.getGenre());
        dto.setDescription(interest.getDescription());
        dto.setLiked(interest.getLiked());
        dto.setRating(interest.getRating());
        dto.setCurrent(interest.isCurrent());
        dto.setSortOrder(interest.getSortOrder());
        dto.setCreatedAt(interest.getCreatedAt());
        dto.setUpdatedAt(interest.getUpdatedAt());

        if (interest.getCreatedAt() != null) {
            dto.setFormattedCreatedAt(interest.getCreatedAt().format(formatter));
        }

        if (interest.getUpdatedAt() != null) {
            dto.setFormattedUpdatedAt(interest.getUpdatedAt().format(formatter));
        }

        dto.setEdited(interest.isEdited());

        return dto;
    }
}
