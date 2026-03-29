package com.memorygrove.backend.filestorage.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO (Data Transfer Object) для ответа со списком фотографий.
 * <p>
 * Содержит не только массив фотографий, но и метаинформацию:
 * общее количество, статистику по годам и другую агрегированную информацию.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ со списком фотографий")
public class PhotoListResponseDto {

    /**
     * Массив фотографий.
     */
    @Schema(
            description = "Список фотографий",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("photos")
    private List<PhotoResponseDto> photos;

    /**
     * Общее количество фотографий.
     */
    @Schema(
            description = "Общее количество фотографий",
            example = "42",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("totalCount")
    private long totalCount;

    /**
     * Суммарный размер всех фотографий в байтах.
     */
    @Schema(
            description = "Общий размер всех фотографий (байт)",
            example = "15728640",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("totalSize")
    private long totalSize;

    /**
     * Суммарный размер в человекочитаемом формате.
     */
    @Schema(
            description = "Общий размер (человекочитаемый)",
            example = "15 MB",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("formattedTotalSize")
    private String formattedTotalSize;

    /**
     * Список годов, за которые есть фотографии.
     */
    @Schema(
            description = "Список годов с фотографиями",
            example = "[2023, 2022, 2021]",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("years")
    private List<Integer> years;
}
