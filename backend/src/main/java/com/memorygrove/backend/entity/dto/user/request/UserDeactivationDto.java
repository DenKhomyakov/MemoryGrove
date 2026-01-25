package com.memorygrove.backend.entity.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ToDo: Сделать справочник причин деактивации пользователя
/**
 * DTO для деактивации пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для деактивации пользователя")
public class UserDeactivationDto {

    /**
     * Дата и время деактивации пользователя
     */
    @Schema(
            description = "Дата и время деактивации пользователя в формате ГГГГ-ММ-ДД Т ЧЧ-ММ-СС",
            example = "2025-01-21T19:24:00",
            nullable = true
    )
    @JsonProperty("deactivatedAt")
    @Description("Дата и время деактивации пользователя")
    private LocalDateTime deactivatedAt;

    /**
     * Причина деактивации пользователя
     */
    @Size(max = 500, message = "Длина причины деактивации должна быть до 500 символов")
    @Schema(
            description = "Причина деактивации пользователя",
            example = "Добровольная деактивация по желанию пользователя",
            maxLength = 500,
            nullable = true
    )
    @JsonProperty("reason")
    @Description("Причина деактивации пользователя")
    private String reason;
}
