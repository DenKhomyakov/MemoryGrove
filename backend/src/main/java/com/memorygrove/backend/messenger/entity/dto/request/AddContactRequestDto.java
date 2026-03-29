package com.memorygrove.backend.messenger.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на добавление нового доверенного контакта
 * <p>
 * Содержит идентификатор пользователя, которого нужно добавить в контакты,
 * и опциональный никнейм для удобства отображения.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на добавление доверенного контакта")
public class AddContactRequestDto {

    /**
     * Идентификатор пользователя, которого добавляем в контакты
     */
    @NotBlank(message = "ID контакта обязателен")
    @Schema(
            description = "ID пользователя, которого добавляем в контакты",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("contactId")
    private String contactId;

    /**
     * Никнейм для отображения (как называть этот контакт)
     * <p>
     * Например: "Сын Петя", "Доктор Иванова"
     * </p>
     */
    @Size(max = 100, message = "Длина никнейма не должна превышать 100 символов")
    @Schema(
            description = "Никнейм для отображения",
            example = "Сын Петя",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("nickname")
    private String nickname;
}
