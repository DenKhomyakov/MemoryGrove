package com.memorygrove.backend.dictionary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Роль пользователя в системе
 *
 * PATIENT - Пациент
 * DOCTOR - Доктор
 * FAMILY_MEMBER - Член семьи
 * OTHER_HALF - Вторая половинка / партнёр
 * FRIEND - Друг
 * COLLEAGUE - Коллега
 * OTHER - Другой вариант, не предусмотренный списком
 * ADMINISTRATOR - Администратор (суперпользователь)
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    /**
     * ID роли пользователя
     * Соответствует константам из вложенного класса Constants
     */
    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Наименование роли пользователя
     * (на русском языке)
     */
    @Column(name = "name", nullable = false)
    private String name;

    public static class Constants {

        /**
         * Пациент
         */
        public final static UUID PATIENT = UUID.fromString("931ff329-8baa-4353-abbc-a0f389e9b07d");

        /**
         * Доктор
         */
        public final static UUID DOCTOR = UUID.fromString("b5541f4d-1517-4848-8ea0-1e306570633b");

        /**
         * Член семьи
         */
        public final static UUID FAMILY_MEMBER = UUID.fromString("f722aa50-e167-4f30-a6d5-17becd3b1028");

        /**
         * Вторая половинка / партнёр
         */
        public final static UUID OTHER_HALF = UUID.fromString("a4d6d594-898c-4cef-97a4-85893df09488");

        /**
         * Друг
         */
        public final static UUID FRIEND = UUID.fromString("6ecb8283-f720-473b-9b89-5b0d2ece01d3");

        /**
         * Коллега
         */
        public final static UUID COLLEAGUE = UUID.fromString("ba1171bd-57d5-4a72-97f5-638be6295a2a");

        /**
         * Другой вариант, не предусмотренный списком
         */
        public final static UUID OTHER = UUID.fromString("f3dc7b85-d1b1-4246-bb22-4103070fb697");

        /**
         * Администратор (суперпользователь)
         */
        public final static UUID ADMINISTRATOR = UUID.fromString("6c863ae5-849c-47ff-ac76-b63830cae231");
    }
}
