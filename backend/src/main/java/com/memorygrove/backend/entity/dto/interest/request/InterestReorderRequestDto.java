package com.memorygrove.backend.entity.dto.interest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для запроса на изменение порядка сортировки интересов
 * <p>
 * Принимает список идентификаторов интересов в том порядке, в котором они должны
 * отображаться. Используется для drag-and-drop сортировки на фронтенде.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на изменение порядка сортировки интересов")
public class InterestReorderRequestDto {

    /**
     * Список идентификаторов интересов в нужном порядке отображения
     * <p>
     * Первый элемент списка получит sortOrder = 1,
     * второй - sortOrder = 2 и так далее.
     * Все ID должны принадлежать текущему пользователю.
     * </p>
     */
    @NotNull(message = "Список идентификаторов интересов обязателен")
    @Schema(
            description = "Список ID интересов в нужном порядке отображения",
            example = "[\"73ba18af-2af1-4950-8a9e-6906a04b5461\", \"83ba18af-2af1-4950-8a9e-6906a04b5461\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("interestIds")
    private List<UUID> interestIds;
}
