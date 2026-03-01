package com.memorygrove.backend.entity.dto.skill.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для запроса на изменение порядка сортировки навыков
 * <p>
 * Принимает список идентификаторов навыков в том порядке, в котором они должны
 * отображаться. Используется для drag-and-drop сортировки на фронтенде.
 * Порядок применяется глобально ко всем навыкам пользователя.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на изменение порядка сортировки навыков")
public class SkillReorderRequestDto {

    /**
     * Список идентификаторов навыков в нужном порядке отображения
     * <p>
     * Первый элемент списка получит sortOrder = 1,
     * второй - sortOrder = 2 и так далее.
     * Все ID должны принадлежать текущему пользователю.
     * </p>
     */
    @NotNull(message = "Список идентификаторов навыков обязателен")
    @Schema(
            description = "Список ID навыков в нужном порядке отображения",
            example = "[\"73ba18af-2af1-4950-8a9e-6906a04b5461\", \"83ba18af-2af1-4950-8a9e-6906a04b5461\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("skillIds")
    private List<UUID> skillIds;
}
