package com.memorygrove.backend.filestorage.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.filestorage.entity.dbo.MemoryPhoto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для ответа с данными о фотографии.
 * <p>
 * Содержит полную информацию о фотографии, включая все метаданные,
 * а также URL для доступа к файлу и миниатюре. Используется при отправке
 * данных на фронтенд после загрузки или при запросе списка фотографий.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными о фотографии")
public class PhotoResponseDto {

    /**
     * Уникальный идентификатор фотографии.
     */
    @Schema(
            description = "Уникальный идентификатор фотографии",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    /**
     * Идентификатор пользователя, загрузившего фотографию.
     */
    @Schema(
            description = "ID пользователя",
            example = "83ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "userId", access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    /**
     * Оригинальное имя файла.
     */
    @Schema(
            description = "Оригинальное имя файла",
            example = "family_vacation_2023.jpg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "fileName", access = JsonProperty.Access.READ_ONLY)
    private String fileName;

    /**
     * URL для скачивания оригинального файла.
     */
    @Schema(
            description = "URL для скачивания файла",
            example = "/api/memory/photos/73ba18af-2af1-4950-8a9e-6906a04b5461/download",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "downloadUrl", access = JsonProperty.Access.READ_ONLY)
    private String downloadUrl;

    /**
     * URL для доступа к миниатюре.
     */
    @Schema(
            description = "URL миниатюры",
            example = "/api/memory/photos/73ba18af-2af1-4950-8a9e-6906a04b5461/thumbnail",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "thumbnailUrl", access = JsonProperty.Access.READ_ONLY)
    private String thumbnailUrl;

    /**
     * Описание фотографии.
     */
    @Schema(
            description = "Описание",
            example = "Наш семейный отпуск в Сочи",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "description", access = JsonProperty.Access.READ_ONLY)
    private String description;

    /**
     * Место съёмки.
     */
    @Schema(
            description = "Место съёмки",
            example = "Сочи, пляж 'Ривьера'",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "location", access = JsonProperty.Access.READ_ONLY)
    private String location;

    /**
     * Год съёмки.
     */
    @Schema(
            description = "Год съёмки",
            example = "2023",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "year", access = JsonProperty.Access.READ_ONLY)
    private Integer year;

    /**
     * Люди на фотографии.
     */
    @Schema(
            description = "Люди на фотографии",
            example = "Анна, Пётр, дети",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "people", access = JsonProperty.Access.READ_ONLY)
    private String people;

    /**
     * Дата и время загрузки.
     */
    @Schema(
            description = "Дата загрузки",
            example = "2026-03-02T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "uploadDate", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime uploadDate;

    /**
     * Отформатированная дата загрузки для отображения.
     */
    @Schema(
            description = "Отформатированная дата загрузки",
            example = "2 марта 2026, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "formattedUploadDate", access = JsonProperty.Access.READ_ONLY)
    private String formattedUploadDate;

    /**
     * Размер файла в байтах.
     */
    @Schema(
            description = "Размер файла (байт)",
            example = "3145728",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "fileSize", access = JsonProperty.Access.READ_ONLY)
    private Long fileSize;

    /**
     * Размер файла в удобочитаемом формате.
     */
    @Schema(
            description = "Размер файла (человекочитаемый)",
            example = "3 MB",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "formattedFileSize", access = JsonProperty.Access.READ_ONLY)
    private String formattedFileSize;

    /**
     * MIME-тип файла.
     */
    @Schema(
            description = "MIME-тип",
            example = "image/jpeg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "contentType", access = JsonProperty.Access.READ_ONLY)
    private String contentType;

    /**
     * Дата создания записи.
     */
    @Schema(
            description = "Дата создания записи",
            example = "2026-03-02T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    /**
     * Дата последнего редактирования.
     */
    @Schema(
            description = "Дата последнего редактирования",
            example = "2026-03-02T16:45:00",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "updatedAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    /**
     * Флаг, было ли редактирование.
     */
    @Schema(
            description = "Было ли редактирование",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isEdited", access = JsonProperty.Access.READ_ONLY)
    private boolean isEdited;

    /**
     * Конвертирует сущность MemoryPhoto в DTO для ответа.
     *
     * @param photo сущность фотографии
     * @return DTO с данными фотографии
     */
    public static PhotoResponseDto fromEntity(MemoryPhoto photo) {
        PhotoResponseDto dto = new PhotoResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        dto.setId(photo.getId());
        dto.setUserId(photo.getUserId());
        dto.setFileName(photo.getFileName());
        dto.setDownloadUrl(photo.getDownloadUrl());
        dto.setThumbnailUrl(photo.getThumbnailUrl());
        dto.setDescription(photo.getDescription());
        dto.setLocation(photo.getLocation());
        dto.setYear(photo.getYear());
        dto.setPeople(photo.getPeople());
        dto.setUploadDate(photo.getUploadDate());
        dto.setFileSize(photo.getFileSize());
        dto.setContentType(photo.getContentType());
        dto.setCreatedAt(photo.getCreatedAt());
        dto.setUpdatedAt(photo.getUpdatedAt());
        dto.setEdited(photo.isEdited());

        if (photo.getUploadDate() != null) {
            dto.setFormattedUploadDate(photo.getUploadDate().format(formatter));
        }

        if (photo.getFileSize() != null) {
            dto.setFormattedFileSize(formatFileSize(photo.getFileSize()));
        }

        return dto;
    }

    /**
     * Форматирует размер файла в человекочитаемый вид.
     *
     * @param size размер в байтах
     * @return строка с размером (например, "2.5 MB")
     */
    private static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }
}
