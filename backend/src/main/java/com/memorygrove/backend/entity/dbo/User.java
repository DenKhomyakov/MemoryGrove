package com.memorygrove.backend.entity.dbo;

import com.memorygrove.backend.dictionary.Role;
import com.memorygrove.backend.dictionary.Sex;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

/**
 * Сущность пользователь
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    /**
     * ID пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Фамилия пользователя
     */
    @NotBlank(message = "Поле 'Фамилия' обязательно")
    @Size(min = 3, max = 50, message = "Длина фамилии должна быть от 3 до 50 символов")
    @Column(name = "surname", nullable = false)
    private String surname;

    /**
     * Имя пользователя
     */
    @NotBlank(message = "Поле 'Имя' обязательно")
    @Size(min = 3, max = 50, message = "Длина имени должна быть от 3 до 50 символов")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Отчество пользователя
     */
    @Size(max = 50, message = "Длина отчества должна быть до 50 символов")
    @Column(name = "middle_name")
    private String middleName;

    /**
     * Дата рождения пользователя
     */
    @NotNull(message = "Поле 'Дата рождения' обязательно")
    @Past(message = "Дата рождения должна быть в прошлом")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * ID пола пользователя
     */
    @NotNull(message = "Поле 'Пол' обязательно")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sex_id", nullable = false)
    private Sex sex;

    /**
     * ID роли пользователя
     */
    @NotNull(message = "Поле 'Роль' обязательно")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * Электронная почта пользователя
     */
    @NotBlank(message = "Поле 'Электронная почта' обязательно")
    @Email(message = "Некорректный формат электронной почты")
    @Size(min = 5, max = 100, message = "Длина электронной почты должна быть от 5 до 100 символов")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Номер телефона пользователя
     */
    @NotBlank(message = "Поле 'Номер телефона' обязательно")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Некорректный формат телефона")
    @Size(max = 20, message = "Длина номера телефона должна быть до 20 символов")
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    /**
     * Адрес пользователя
     */
    @Size(max = 250, message = "Длина адреса должна быть до 250 символов")
    @Column(name = "address")
    private String address;

    /**
     * Хэш пароля пользователя
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Дата и время создания аккаунта пользователя
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последней авторизации пользователя
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * Получение полного имени пользователя
     */
    public String getFullName() {
        return String.format("%s %s %s", surname, name, middleName).trim();
    }

    /**
     * Получение возраста пользователя
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }

        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Проверка роли пользователя на то, является ли он пациентом
     */
    public boolean isPatient() {
        return role != null && Role.Constants.PATIENT.equals(role.getId());
    }

    /**
     * Проверка роли пользователя на то, является ли он доктором
     */
    public boolean isDoctor() {
        return role != null && Role.Constants.DOCTOR.equals(role.getId());
    }

    /**
     * Проверка роли пользователя на то, является ли он администратором
     */
    public boolean isAdministrator() {
        return role != null && Role.Constants.ADMINISTRATOR.equals(role.getId());
    }
}
