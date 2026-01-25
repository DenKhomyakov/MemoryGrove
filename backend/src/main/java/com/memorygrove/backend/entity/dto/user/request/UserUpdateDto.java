package com.memorygrove.backend.entity.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.dictionary.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// ToDo: Сделать функционал по смене роли через API
/**
 * DTO для обновления данных пользователя
 * (не содержит электронную почту, пароль)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания нового пользователя")
public class UserUpdateDto {

    /**
     * Фамилия пользователя
     */
    @NotBlank(message = "Поле 'Фамилия' обязательно")
    @Size(min = 3, max = 50, message = "Длина фамилии должна быть от 3 до 50 символов")
    @Schema(
            description = "Фамилия пользователя",
            example = "Лунев",
            minLength = 3,
            maxLength = 50
    )
    @JsonProperty("surname")
    @Description("Фамилия пользователя")
    private String surname;

    /**
     * Имя пользователя
     */
    @NotBlank(message = "Поле 'Имя' обязательно")
    @Size(min = 3, max = 50, message = "Длина имени должна быть от 3 до 50 символов")
    @Schema(
            description = "Имя пользователя",
            example = "Геннадий",
            minLength = 3,
            maxLength = 50
    )
    @JsonProperty("name")
    @Description("Имя пользователя")
    private String name;

    /**
     * Отчество пользователя
     */
    @Size(max = 50, message = "Длина отчества должна быть до 50 символов")
    @Schema(
            description = "Отчество пользователя",
            example = "Александрович",
            maxLength = 50,
            nullable = true
    )
    @JsonProperty("middleName")
    @Description("Отчество пользователя")
    private String middleName;

    /**
     * Дата рождения пользователя
     */
    @NotNull(message = "Поле 'Дата рождения' обязательно")
    @Past(message = "Дата рождения должна быть в прошлом")
    @Schema(
            description = "Дата рождения пользователя в формате ГГГГ-ММ-ДД",
            example = "1990-06-28"
    )
    @JsonProperty("birthDate")
    @Description("Дата рождения пользователя")
    private LocalDate birthDate;

    /**
     * Пол пользователя
     */
    @NotNull(message = "Поле 'Пол' обязательно")
    @Schema(
            description = "Пол пользователя",
            example = "6b426722-1c17-4900-ab99-f3374a8e3880 (мужской пол)",
            implementation = Sex.class
    )
    @JsonProperty("sex")
    @Description("Пол пользователя")
    private Sex sex;

    /**
     * Номер телефона пользователя
     */
    @NotBlank(message = "Поле 'Номер телефона' обязательно")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Некорректный формат телефона")
    @Size(max = 20, message = "Длина номера телефона должна быть до 20 символов")
    @Schema(
            description = "Номер телефона пользователя",
            example = "+79830000000",
            maxLength = 20,
            pattern = "^\\+?[1-9]\\d{1,14}$"
    )
    @JsonProperty("phone")
    @Description("Номер телефона пользователя")
    private String phone;

    /**
     * Адрес пользователя
     */
    @Size(max = 250, message = "Длина адреса должна быть до 250 символов")
    @Schema(
            description = "Адрес пользователя",
            example = "г. Томск, ул. Красноармейская, д. 154, кв. 345",
            maxLength = 250,
            nullable = true
    )
    @JsonProperty("address")
    @Description("Адрес пользователя")
    private String address;

    /**
     * Описание аккаунта пользователя
     */
    @Size(max = 500, message = "Длина описания должна быть до 500 символов")
    @Schema(
            description = "Описание аккаунта пользователя",
            example = "Являюсь врачом-неврологом." +
                    "В 2014 году с отличием окончил СибГМУ по специальности 'Лечебное дело'." +
                    "В 2016 окончил ординатуру по неврологии на базе СибГМУ",
            maxLength = 500,
            nullable = true
    )
    @JsonProperty("description")
    @Description("Описание аккаунта пользователя")
    private String description;
}
