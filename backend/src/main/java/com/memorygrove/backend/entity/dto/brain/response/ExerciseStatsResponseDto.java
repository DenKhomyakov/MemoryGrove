package com.memorygrove.backend.entity.dto.brain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.dictionary.ExerciseType;
import com.memorygrove.backend.entity.dbo.ExerciseStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для ответа со статистикой упражнения
 * <p>
 * Содержит полную статистику по конкретному типу упражнения для пользователя.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ со статистикой упражнения")
public class ExerciseStatsResponseDto {

    /**
     * Уникальный идентификатор записи статистики
     */
    @Schema(
            description = "ID записи статистики",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    /**
     * Тип упражнения
     */
    @Schema(
            description = "Тип упражнения",
            example = "ARITHMETIC",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "exerciseType", access = JsonProperty.Access.READ_ONLY)
    private ExerciseType exerciseType;

    /**
     * Отображаемое название упражнения
     */
    @Schema(
            description = "Отображаемое название упражнения",
            example = "Арифметическая цепочка",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "displayName", access = JsonProperty.Access.READ_ONLY)
    private String displayName;

    /**
     * Общее количество попыток
     */
    @Schema(
            description = "Количество попыток",
            example = "10",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "attempts", access = JsonProperty.Access.READ_ONLY)
    private int attempts;

    /**
     * Количество успешных попыток
     */
    @Schema(
            description = "Количество успешных попыток",
            example = "7",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "successes", access = JsonProperty.Access.READ_ONLY)
    private int successes;

    /**
     * Процент успешных попыток
     */
    @Schema(
            description = "Процент успеха",
            example = "70",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "successRate", access = JsonProperty.Access.READ_ONLY)
    private int successRate;

    /**
     * Лучший результат
     */
    @Schema(
            description = "Лучший результат",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "bestScore", access = JsonProperty.Access.READ_ONLY)
    private Integer bestScore;

    /**
     * Лучшее время в секундах
     */
    @Schema(
            description = "Лучшее время (секунд)",
            example = "32",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "bestTimeSeconds", access = JsonProperty.Access.READ_ONLY)
    private Integer bestTimeSeconds;

    /**
     * Отформатированное лучшее время
     */
    @Schema(
            description = "Отформатированное лучшее время",
            example = "00:32",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "formattedBestTime", access = JsonProperty.Access.READ_ONLY)
    private String formattedBestTime;

    /**
     * Дата последней попытки
     */
    @Schema(
            description = "Дата последней попытки",
            example = "2026-03-08T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "lastAttemptAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastAttemptAt;

    /**
     * Отформатированная дата последней попытки
     */
    @Schema(
            description = "Отформатированная дата последней попытки",
            example = "8 марта 2026, 15:30",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "formattedLastAttemptAt", access = JsonProperty.Access.READ_ONLY)
    private String formattedLastAttemptAt;

    /**
     * Конвертирует сущность ExerciseStats в DTO для ответа
     *
     * @param stats сущность статистики
     * @return DTO с данными статистики
     */
    public static ExerciseStatsResponseDto fromEntity(ExerciseStats stats) {
        ExerciseStatsResponseDto dto = new ExerciseStatsResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

        dto.setId(stats.getId());
        dto.setExerciseType(stats.getExerciseType());
        dto.setDisplayName(getDisplayName(stats.getExerciseType()));
        dto.setAttempts(stats.getAttempts());
        dto.setSuccesses(stats.getSuccesses());
        dto.setSuccessRate(stats.getSuccessRate());
        dto.setBestScore(stats.getBestScore());
        dto.setBestTimeSeconds(stats.getBestTimeSeconds());
        dto.setLastAttemptAt(stats.getLastAttemptAt());

        if (stats.getBestTimeSeconds() != null) {
            int minutes = stats.getBestTimeSeconds() / 60;
            int seconds = stats.getBestTimeSeconds() % 60;
            dto.setFormattedBestTime(String.format("%02d:%02d", minutes, seconds));
        }

        if (stats.getLastAttemptAt() != null) {
            dto.setFormattedLastAttemptAt(stats.getLastAttemptAt().format(formatter));
        }

        return dto;
    }

    /**
     * Получает отображаемое название упражнения по его типу
     *
     * @param type тип упражнения
     * @return отображаемое название
     */
    private static String getDisplayName(ExerciseType type) {
        switch (type) {
            case ARITHMETIC:
                return "🧮 Арифметическая цепочка";
            case TEXT_COMPREHENSION:
                return "📖 Понимание текста";
            case POEM:
                return "📝 Заучивание стихов";
            case ASSOCIATION:
                return "🔗 Ассоциации";
            case SEQUENCE:
                return "📊 Продолжи ряд";
            case REVERSE_WORD:
                return "🔄 Слово наоборот";
            case ODD_ONE_OUT:
                return "❓ Что лишнее?";
            case SCHULTE_TABLE:
                return "🔢 Таблица Шульте";
            default:
                return type.name();
        }
    }
}
