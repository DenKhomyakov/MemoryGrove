package com.memorygrove.backend.filestorage.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для обновления метаданных существующей фотографии.
 * <p>
 * Используется для изменения описания, места съёмки, года и списка людей
 * без повторной загрузки самого файла. Применяется в PUT/PATCH запросах.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление метаданных фотографии")
public class PhotoUpdateMetadataRequestDto {

    /**
     * Новое описание фотографии.
     * <p>
     * Заменяет существующее описание. Если передано null или пустая строка,
     * описание будет удалено.
     * </p>
     */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Schema(
            description = "Описание фотографии",
            example = "Обновлённое описание: наш семейный отпуск",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("description")
    private String description;

    /**
     * Новое место съёмки.
     */
    @Size(max = 255, message = "Название места не должно превышать 255 символов")
    @Schema(
            description = "Место съёмки",
            example = "Сочи, Красная Поляна",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("location")
    private String location;

    /**
     * Новый год съёмки.
     */
    @Schema(
            description = "Год съёмки",
            example = "2024",
            minimum = "1900",
            maximum = "2100",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("year")
    private Integer year;

    /**
     * Новый список людей на фотографии.
     */
    @Size(max = 500, message = "Список людей не должен превышать 500 символов")
    @Schema(
            description = "Люди на фотографии (имена через запятую)",
            example = "Анна, Пётр, Мария",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("people")
    private String people;
}
