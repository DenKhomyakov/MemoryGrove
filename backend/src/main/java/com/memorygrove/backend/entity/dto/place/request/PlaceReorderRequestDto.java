package com.memorygrove.backend.entity.dto.place.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для запроса на изменение порядка сортировки мест
 * <p>
 * Содержит список идентификаторов мест в том порядке, в котором они должны
 * отображаться. Порядок элементов в списке определяет новый порядок сортировки.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на изменение порядка сортировки мест")
public class PlaceReorderRequestDto {

    /**
     * Список идентификаторов мест в нужном порядке отображения
     * <p>
     * Первый элемент списка получит sortOrder = 1, второй - sortOrder = 2 и т.д.
     * Все ID должны принадлежать текущему пользователю.
     * </p>
     */
    @NotNull(message = "Список идентификаторов мест обязателен")
    @Schema(
            description = "Список ID мест в нужном порядке отображения",
            example = "[\"73ba18af-2af1-4950-8a9e-6906a04b5461\", \"83ba18af-2af1-4950-8a9e-6906a04b5461\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("placeIds")
    private List<UUID> placeIds;
}
