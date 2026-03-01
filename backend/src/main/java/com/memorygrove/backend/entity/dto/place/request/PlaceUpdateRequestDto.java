package com.memorygrove.backend.entity.dto.place.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для обновления существующего значимого места
 * <p>
 * Содержит поля, которые можно изменить в уже созданном месте.
 * Включает все те же поля, что и при создании, плюс возможность
 * изменения порядка сортировки.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление существующего места")
public class PlaceUpdateRequestDto {

    /**
     * Обновлённое название места
     */
    @NotBlank(message = "Название места не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов")
    @Schema(
            description = "Название места",
            example = "Городская поликлиника №1 (обновлено)",
            minLength = 2,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    private String title;

    /**
     * Обновлённый адрес места
     */
    @NotBlank(message = "Адрес не может быть пустым")
    @Size(max = 250, message = "Адрес не должен превышать 250 символов")
    @Schema(
            description = "Полный адрес места",
            example = "г. Томск, ул. Кирова, д. 25, корпус 2",
            maxLength = 250,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("address")
    private String address;

    /**
     * Обновлённое описание или заметки
     */
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Schema(
            description = "Дополнительное описание или заметки о месте",
            example = "Принимает невролог Петрова А.И., кабинет 315, по вторникам и четвергам с 9:00 до 15:00",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    @JsonProperty("description")
    private String description;

    /**
     * Обновлённая категория места
     */
    @Schema(
            description = "Категория места для классификации",
            example = "Медицина",
            allowableValues = {"Дом", "Медицина", "Работа", "Отдых", "Образование", "Другое"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("category")
    private String category;

    /**
     * Обновлённый флаг домашнего адреса
     */
    @Schema(
            description = "Флаг домашнего адреса",
            example = "false",
            defaultValue = "false"
    )
    @JsonProperty("isHome")
    private boolean isHome;

    /**
     * Обновлённый флаг места работы/учёбы
     */
    @Schema(
            description = "Флаг места работы/учёбы",
            example = "false",
            defaultValue = "false"
    )
    @JsonProperty("isWork")
    private boolean isWork;

    /**
     * Обновлённый флаг медицинского учреждения
     */
    @Schema(
            description = "Флаг медицинского учреждения",
            example = "true",
            defaultValue = "false"
    )
    @JsonProperty("isMedical")
    private boolean isMedical;

    /**
     * Порядок сортировки
     * <p>
     * Числовое значение, определяющее позицию места в списке.
     * Меньшее число = выше в списке.
     * </p>
     */
    @Schema(
            description = "Порядок сортировки (меньше = выше в списке)",
            example = "1",
            minimum = "1"
    )
    @JsonProperty("sortOrder")
    private Integer sortOrder;
}
