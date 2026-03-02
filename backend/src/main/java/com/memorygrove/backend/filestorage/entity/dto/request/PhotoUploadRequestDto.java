package com.memorygrove.backend.filestorage.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO (Data Transfer Object) для загрузки новой фотографии.
 * <p>
 * Содержит файл изображения и метаданные: описание, место съёмки,
 * год и список людей на фотографии. Используется при POST запросе
 * на эндпоинт /api/memory/photos/upload.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на загрузку новой фотографии")
public class PhotoUploadRequestDto {

    /**
     * Файл изображения для загрузки.
     * <p>
     * Должен быть одного из допустимых типов: JPEG, PNG, GIF, WebP.
     * Размер не должен превышать максимально допустимый (по умолчанию 10 MB).
     * </p>
     */
    @Schema(
            description = "Файл изображения",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("file")
    private MultipartFile file;

    /**
     * Описание фотографии.
     * <p>
     * Произвольный текст, описывающий содержание или контекст фотографии.
     * Например: "Наш семейный отпуск в Сочи" или "Первый день в школе".
     * </p>
     */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Schema(
            description = "Описание фотографии",
            example = "Наш семейный отпуск в Сочи, 2023 год",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("description")
    private String description;

    /**
     * Место съёмки.
     * <p>
     * Географическое или описательное название места, где была сделана фотография.
     * Например: "Москва, Красная площадь" или "Домашний архив".
     * </p>
     */
    @Size(max = 255, message = "Название места не должно превышать 255 символов")
    @Schema(
            description = "Место съёмки",
            example = "Сочи, пляж 'Ривьера'",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("location")
    private String location;

    /**
     * Год, к которому относится фотография.
     * <p>
     * Может не совпадать с годом загрузки. Например, можно загрузить
     * отсканированную фотографию из семейного архива 1985 года.
     * </p>
     */
    @Schema(
            description = "Год съёмки",
            example = "2023",
            minimum = "1900",
            maximum = "2100",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("year")
    private Integer year;

    /**
     * Люди на фотографии.
     * <p>
     * Имена людей, изображённых на фотографии. Можно указывать через запятую
     * или в формате JSON массива для более сложной структуры.
     * </p>
     */
    @Size(max = 500, message = "Список людей не должен превышать 500 символов")
    @Schema(
            description = "Люди на фотографии (имена через запятую)",
            example = "Анна, Пётр, дети",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("people")
    private String people;
}
