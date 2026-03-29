package com.memorygrove.backend.entity.dto.skill.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для обновления существующего навыка
 * <p>
 * Содержит все поля, которые можно изменить в уже созданном навыке,
 * включая возможность изменения статуса выполнения, даты последнего
 * выполнения, сложности, важности и порядка сортировки.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление навыка")
public class SkillUpdateRequestDto {

    /**
     * Категория навыка
     * <p>
     * Определяет тип навыка. Допустимые значения:
     * <ul>
     *   <li>SELF_CARE - Самообслуживание (чистить зубы, одеваться)</li>
     *   <li>HOUSEHOLD - Быт (готовка, уборка)</li>
     *   <li>COMMUNICATION - Коммуникация (звонки, сообщения)</li>
     *   <li>MOBILITY - Мобильность (прогулки, транспорт)</li>
     *   <li>HEALTH - Здоровье (лекарства, измерения)</li>
     *   <li>COGNITIVE - Когнитивные (чтение, счёт)</li>
     *   <li>OTHER - Другое</li>
     * </ul>
     * </p>
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
     * <p>
     * Краткое, понятное название действия. Например: "Чистить зубы",
     * "Приготовить чай", "Позвонить родственникам"
     * </p>
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
     * <p>
     * Подробное описание того, что именно нужно делать, возможные инструкции
     * или напоминания о последовательности действий.
     * </p>
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
     * Флаг, может ли пользователь выполнять навык сейчас
     * <p>
     * true - пользователь может выполнить действие самостоятельно
     * false - пока не может или нужна помощь
     * </p>
     */
    @Schema(
            description = "Может ли выполнять сейчас",
            example = "true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("canDoNow")
    private Boolean canDoNow;

    /**
     * Дата и время последнего успешного выполнения
     * <p>
     * Используется для отслеживания прогресса и частоты выполнения.
     * Может быть null, если навык ещё ни разу не выполнялся.
     * </p>
     */
    @Schema(
            description = "Дата последнего выполнения",
            example = "2026-03-01T10:30:00",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("lastPerformedAt")
    private LocalDateTime lastPerformedAt;

    /**
     * Сложность навыка по шкале от 1 до 5
     * <p>
     * Оценка того, насколько сложно выполнить этот навык:
     * 1 - очень легко, 5 - очень сложно
     * </p>
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
     * Важность навыка по шкале от 1 до 5
     * <p>
     * Насколько важно сохранить этот навык для самостоятельной жизни:
     * 1 - не очень важно, 5 - критически важно
     * </p>
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
     * Заметки или наблюдения
     * <p>
     * Дополнительная информация о навыке: что помогает, что мешает,
     * какие есть особенности выполнения.
     * </p>
     */
    @Size(max = 500, message = "Заметки не должны превышать 500 символов")
    @Schema(
            description = "Заметки или наблюдения",
            example = "Нужна помощь с выдавливанием пасты, но чистит самостоятельно",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("notes")
    private String notes;

    /**
     * Порядок сортировки внутри категории
     * <p>
     * Числовое значение, определяющее позицию навыка в списке внутри категории.
     * Меньшее число = выше в списке.
     * </p>
     */
    @Schema(
            description = "Порядок сортировки (меньше = выше в списке)",
            example = "1",
            minimum = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("sortOrder")
    private Integer sortOrder;
}
