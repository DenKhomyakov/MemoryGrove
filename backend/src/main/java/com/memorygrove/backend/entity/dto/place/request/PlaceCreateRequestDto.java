package com.memorygrove.backend.entity.dto.place.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для создания нового значимого места
 * <p>
 * Содержит все необходимые поля для добавления нового места в список
 * значимых локаций пользователя. Включает название, адрес, описание,
 * категорию и флаги для специальных типов мест (дом, работа, медицина).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание нового значимого места")
public class PlaceCreateRequestDto {

    /**
     * Название места
     * <p>
     * Краткое, запоминающееся название локации. Например: "Дом", "Поликлиника",
     * "Любимый парк", "Университет".
     * </p>
     */
    @NotBlank(message = "Название места не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов")
    @Schema(
            description = "Название места",
            example = "Городская поликлиника №1",
            minLength = 2,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("title")
    private String title;

    /**
     * Полный адрес места
     * <p>
     * Точный почтовый адрес или описание местоположения. Может включать
     * город, улицу, номер дома, корпус и т.д.
     * </p>
     */
    @NotBlank(message = "Адрес не может быть пустым")
    @Size(max = 250, message = "Адрес не должен превышать 250 символов")
    @Schema(
            description = "Полный адрес места",
            example = "г. Томск, ул. Кирова, д. 25",
            maxLength = 250,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("address")
    private String address;

    /**
     * Дополнительное описание или заметки
     * <p>
     * Полезная информация о месте: часы работы, контактные данные,
     * особенности посещения, номера кабинетов и т.д.
     * </p>
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
     * Категория места
     * <p>
     * Обобщённая классификация места для удобной фильтрации и группировки.
     * Допустимые значения: "Дом", "Медицина", "Работа", "Отдых", "Образование", "Другое"
     * </p>
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
     * Флаг, указывающий, является ли место домашним адресом
     * <p>
     * Если true, место помечается как основное место проживания пользователя.
     * </p>
     */
    @Schema(
            description = "Флаг домашнего адреса",
            example = "false",
            defaultValue = "false"
    )
    @JsonProperty("isHome")
    private boolean isHome;

    /**
     * Флаг, указывающий, является ли место работой или учебой
     * <p>
     * Если true, место помечается как основное место работы или учёбы пользователя.
     * </p>
     */
    @Schema(
            description = "Флаг места работы/учёбы",
            example = "false",
            defaultValue = "false"
    )
    @JsonProperty("isWork")
    private boolean isWork;

    /**
     * Флаг, указывающий, является ли место медицинским учреждением
     * <p>
     * Если true, место помечается как поликлиника, больница или другое
     * медицинское учреждение, которое регулярно посещает пользователь.
     * </p>
     */
    @Schema(
            description = "Флаг медицинского учреждения",
            example = "true",
            defaultValue = "false"
    )
    @JsonProperty("isMedical")
    private boolean isMedical;
}
