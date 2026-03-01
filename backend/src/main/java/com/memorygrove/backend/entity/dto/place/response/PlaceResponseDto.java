package com.memorygrove.backend.entity.dto.place.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.entity.dbo.SignificantPlace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для ответа API с данными о значимом месте
 * <p>
 * Содержит полную информацию о месте, включая все поля сущности,
 * а также отформатированные даты для удобного отображения на фронтенде.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными о значимом месте")
public class PlaceResponseDto {

    /**
     * Уникальный идентификатор места в системе
     */
    @Schema(
            description = "Уникальный идентификатор места",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    /**
     * Идентификатор пользователя-владельца места
     */
    @Schema(
            description = "ID пользователя, которому принадлежит место",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "userId", access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    /**
     * Название места
     */
    @Schema(
            description = "Название места",
            example = "Городская поликлиника №1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "title", access = JsonProperty.Access.READ_ONLY)
    private String title;

    /**
     * Полный адрес места
     */
    @Schema(
            description = "Полный адрес",
            example = "г. Томск, ул. Кирова, д. 25",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "address", access = JsonProperty.Access.READ_ONLY)
    private String address;

    /**
     * Дополнительное описание или заметки
     */
    @Schema(
            description = "Описание или заметки о месте",
            example = "Принимает невролог Петрова А.И., кабинет 315",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "description", access = JsonProperty.Access.READ_ONLY)
    private String description;

    /**
     * Категория места
     */
    @Schema(
            description = "Категория места",
            example = "Медицина",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "category", access = JsonProperty.Access.READ_ONLY)
    private String category;

    /**
     * Флаг, является ли место домашним адресом
     */
    @Schema(
            description = "Флаг домашнего адреса",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isHome", access = JsonProperty.Access.READ_ONLY)
    private boolean isHome;

    /**
     * Флаг, является ли местом работы/учёбы
     */
    @Schema(
            description = "Флаг места работы/учёбы",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isWork", access = JsonProperty.Access.READ_ONLY)
    private boolean isWork;

    /**
     * Флаг, является ли медицинским учреждением
     */
    @Schema(
            description = "Флаг медицинского учреждения",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isMedical", access = JsonProperty.Access.READ_ONLY)
    private boolean isMedical;

    /**
     * Порядок сортировки
     */
    @Schema(
            description = "Порядок сортировки (меньше = выше в списке)",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "sortOrder", access = JsonProperty.Access.READ_ONLY)
    private Integer sortOrder;

    /**
     * Дата и время создания записи
     */
    @Schema(
            description = "Дата и время создания",
            example = "2026-03-01T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего редактирования
     */
    @Schema(
            description = "Дата и время последнего редактирования",
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
     * Флаг, было ли место отредактировано
     */
    @Schema(
            description = "Флаг, было ли место отредактировано",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isEdited", access = JsonProperty.Access.READ_ONLY)
    private boolean isEdited;

    /**
     * Конвертирует сущность SignificantPlace в DTO для ответа
     *
     * @param place сущность значимого места
     * @return DTO с данными места
     */
    public static PlaceResponseDto fromEntity(SignificantPlace place) {
        PlaceResponseDto dto = new PlaceResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        dto.setId(place.getId());
        dto.setUserId(place.getUser().getId());
        dto.setTitle(place.getTitle());
        dto.setAddress(place.getAddress());
        dto.setDescription(place.getDescription());
        dto.setCategory(place.getCategory());
        dto.setHome(place.isHome());
        dto.setWork(place.isWork());
        dto.setMedical(place.isMedical());
        dto.setSortOrder(place.getSortOrder());
        dto.setCreatedAt(place.getCreatedAt());
        dto.setUpdatedAt(place.getUpdatedAt());

        if (place.getCreatedAt() != null) {
            dto.setFormattedCreatedAt(place.getCreatedAt().format(formatter));
        }

        if (place.getUpdatedAt() != null) {
            dto.setFormattedUpdatedAt(place.getUpdatedAt().format(formatter));
        }

        dto.setEdited(place.getUpdatedAt() != null && !place.getUpdatedAt().equals(place.getCreatedAt()));

        return dto;
    }
}
