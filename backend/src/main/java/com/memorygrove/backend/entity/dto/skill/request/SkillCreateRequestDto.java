package com.memorygrove.backend.entity.dto.skill.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для создания нового навыка
 * <p>
 * Содержит все необходимые поля для добавления нового навыка в анкету пользователя.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание нового навыка")
public class SkillCreateRequestDto {

    /**
     * Категория навыка
     */
    @NotBlank(message = "Категория обязательна")
    @Schema(
            description = "Категория навыка",
            example = "SELF_CARE",
            allowableValues = {"SELF_CARE", "HOUSEHOLD", "COMMUNICATION", "MOBILITY", "HEALTH", "COGNITIVE", "OTHER"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("category")
    private String category;

    /**
     * Название навыка
     */
    @NotBlank(message = "Название навыка не может быть пустым")
    @Size(min = 3, max = 200, message = "Название должно содержать от 3 до 200 символов")
    @Schema(
            description = "Название навыка",
            example = "Чистить зубы",
            minLength = 3,
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    private String title;

    /**
     * Описание навыка
     */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Schema(
            description = "Описание навыка",
            example = "Почистить зубы щёткой с пастой, утром и вечером",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("description")
    private String description;

    /**
     * Флаг, может ли выполнять сейчас
     */
    @Schema(
            description = "Может ли выполнять сейчас",
            example = "false",
            defaultValue = "false"
    )
    @JsonProperty("canDoNow")
    private boolean canDoNow = false;

    /**
     * Сложность навыка (1-5)
     */
    @Schema(
            description = "Сложность выполнения (1-5)",
            example = "2",
            minimum = "1",
            maximum = "5",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("difficulty")
    private Integer difficulty;

    /**
     * Важность навыка (1-5)
     */
    @Schema(
            description = "Важность навыка (1-5)",
            example = "5",
            minimum = "1",
            maximum = "5",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("importance")
    private Integer importance;

    /**
     * Заметки
     */
    @Size(max = 500, message = "Заметки не должны превышать 500 символов")
    @Schema(
            description = "Заметки или наблюдения",
            example = "Нужна помощь с выдавливанием пасты",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("notes")
    private String notes;
}
