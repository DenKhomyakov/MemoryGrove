package com.memorygrove.backend.entity.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memorygrove.backend.dictionary.Role;
import com.memorygrove.backend.dictionary.Sex;
import com.memorygrove.backend.entity.dbo.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для ответа API с данными пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для ответа API с данными пользователя")
public class UserResponseDto {

    /**
     * ID пользователя
     * (генерируется автоматически при создании записи в БД)
     */
    @Schema(
            description = "Уникальный идентификатор (ID) пользователя",
            example = "73ba18af-2af1-4950-8a9e-6906a04b5461",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    @Description("ID пользователя")
    private UUID id;

    /**
     * Фамилия пользователя
     */
    @Schema(
            description = "Фамилия пользователя",
            example = "Лунев",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "surname", access = JsonProperty.Access.READ_ONLY)
    @Description("Фамилия пользователя")
    private String surname;

    /**
     * Имя пользователя
     */
    @Schema(
            description = "Имя пользователя",
            example = "Геннадий",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "name", access = JsonProperty.Access.READ_ONLY)
    @Description("Имя пользователя")
    private String name;

    /**
     * Отчество пользователя
     */
    @Schema(
            description = "Отчество пользователя",
            example = "Александрович",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "middleName", access = JsonProperty.Access.READ_ONLY)
    @Description("Отчество пользователя")
    private String middleName;

    /**
     * Дата рождения пользователя
     */
    @Schema(
            description = "Дата рождения пользователя в формате ГГГГ-ММ-ДД",
            example = "1990-06-28",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "birthDate", access = JsonProperty.Access.READ_ONLY)
    @Description("Дата рождения пользователя")
    private LocalDate birthDate;

    /**
     * Пол пользователя
     */
    @Schema(
            description = "Пол пользователя",
            example = "6b426722-1c17-4900-ab99-f3374a8e3880 (мужской пол)",
            implementation = Sex.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "sex", access = JsonProperty.Access.READ_ONLY)
    @Description("Пол пользователя")
    private Sex sex;

    /**
     * Роль пользователя
     */
    @Schema(
            description = "Роль пользователя",
            example = "b5541f4d-1517-4848-8ea0-1e306570633b (доктор)",
            implementation = Role.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "role", access = JsonProperty.Access.READ_ONLY)
    @Description("Роль пользователя")
    private Role role;

    /**
     * Электронная почта пользователя
     */
    @Schema(
            description = "Электронная почта пользователя",
            example = "LunevGA@yandex.ru",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "email", access = JsonProperty.Access.READ_ONLY)
    @Description("Электронная почта пользователя")
    private String email;

    /**
     * Номер телефона пользователя
     */
    @Schema(
            description = "Номер телефона пользователя",
            example = "+79830000000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "phone", access = JsonProperty.Access.READ_ONLY)
    @Description("Номер телефона пользователя")
    private String phone;

    /**
     * Адрес пользователя
     */
    @Schema(
            description = "Адрес пользователя",
            example = "г. Томск, ул. Красноармейская, д. 154, кв. 345",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "address", access = JsonProperty.Access.READ_ONLY)
    @Description("Адрес пользователя")
    private String address;

    /**
     * Описание аккаунта пользователя
     */
    @Schema(
            description = "Описание аккаунта пользователя",
            example = "Являюсь врачом-неврологом." +
                    "В 2014 году с отличием окончил СибГМУ по специальности 'Лечебное дело'." +
                    "В 2016 окончил ординатуру по неврологии на базе СибГМУ",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "description", access = JsonProperty.Access.READ_ONLY)
    @Description("Описание аккаунта пользователя")
    private String description;

    /**
     * Дата и время создания аккаунта пользователя
     * (устанавливается автоматически при создании пользователя; не может быть изменено)
     */
    @Schema(
            description = "Дата и время создания аккаунта пользователя в формате ГГГГ-ММ-ДД Т ЧЧ-ММ-СС",
            example = "2025-01-21T19:16:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Дата и время создания аккаунта пользователя")
    private LocalDateTime createdAt;

    /**
     * Дата и время последней авторизации пользователя
     */
    @Schema(
            description = "Дата и время последней авторизации пользователя в формате ГГГГ-ММ-ДД Т ЧЧ-ММ-СС",
            example = "2025-01-21T19:20:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "lastLoginAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Дата и время последней авторизации пользователя")
    private LocalDateTime lastLoginAt;

    /**
     * Статус активности пользователя
     * (если пользователь перестал использовать сервис, то вместо удаления аккаунта флаг устанавливается в false)
     *
     * true - активен (устанавливается по умолчанию)
     * false - неактивен
     */
    @Schema(
            description = "Статус активности пользователя",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "isActive", access = JsonProperty.Access.READ_ONLY)
    @Description("Статус активности пользователя")
    private boolean isActive;

    /**
     * Дата и время деактивации пользователя
     */
    @Schema(
            description = "Дата и время деактивации пользователя в формате ГГГГ-ММ-ДД Т ЧЧ-ММ-СС",
            example = "2025-01-21T19:24:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "deactivatedAt", access = JsonProperty.Access.READ_ONLY)
    @Description("Дата и время деактивации пользователя")
    private LocalDateTime deactivatedAt;

    /**
     * Причина деактивации пользователя
     */
    @Schema(
            description = "Причина деактивации пользователя",
            example = "Добровольная деактивация по желанию пользователя",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "deactivationReason", access = JsonProperty.Access.READ_ONLY)
    @Description("Причина деактивации пользователя")
    private String deactivationReason;

    /**
     * Полное имя пользователя (ФИО)
     * (вычисляемое поле)
     */
    @Schema(
            description = "Полное имя пользователя (ФИО)",
            example = "Лунев Геннадий Александрович",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "fullName", access = JsonProperty.Access.READ_ONLY)
    @Description("Полное имя пользователя (ФИО)")
    private String fullName;

    /**
     * Возраст пользователя (в годах)
     * (вычисляемое поле)
     */
    @Schema(
            description = "Возраст пользователя (в годах)",
            example = "36",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(value = "age", access = JsonProperty.Access.READ_ONLY)
    @Description("Возраст пользователя (в годах)")
    private Integer age;

    /**
     * URL фотографии профиля
     */
    @Schema(
            description = "URL фотографии профиля",
            example = "/api/users/73ba18af-2af1-4950-8a9e-6906a04b5461/photo",
            accessMode = Schema.AccessMode.READ_ONLY,
            nullable = true
    )
    @JsonProperty(value = "photoUrl", access = JsonProperty.Access.READ_ONLY)
    @Description("URL фотографии профиля")
    private String photoUrl;

    /**
     * Метод (как конструктор) для созданеия DTO из сущности User
     * @param user DBO с данными
     * @return DTO с информацией о пользователе
     */
    public static UserResponseDto fromEntity(User user) {
        UserResponseDto dto = new UserResponseDto();

        dto.setId(user.getId());
        dto.setSurname(user.getSurname());
        dto.setName(user.getName());
        dto.setMiddleName(user.getMiddleName());
        dto.setBirthDate(user.getBirthDate());
        dto.setSex(user.getSex());
        dto.setRole(user.getRole());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setDescription(user.getDescription());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setActive(user.isActive());
        dto.setDeactivatedAt(user.getDeactivatedAt());
        dto.setDeactivationReason(user.getDeactivationReason());
        dto.setFullName(user.getFullName());
        dto.setAge(user.getAge());
        dto.setPhotoUrl(user.getPhotoUrl());

        return dto;
    }
}
