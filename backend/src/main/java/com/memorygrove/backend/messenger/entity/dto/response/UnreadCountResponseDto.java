package com.memorygrove.backend.messenger.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа с количеством непрочитанных сообщений
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Количество непрочитанных сообщений")
public class UnreadCountResponseDto {

    /**
     * Общее количество непрочитанных сообщений
     */
    @Schema(
            description = "Всего непрочитанных",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("total")
    private long total;
}
