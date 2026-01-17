package com.memorygrove.backend.dictionary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Справочник полов пользователя
 *
 * MALE - Мужской
 * FEMALE - Женский
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "sexes")
public class Sex {

    /**
     * ID пола пользователя
     * Используется для связи с другими сущностями
     * Соответствует константам из вложенного класса Constants
     */
    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Наименование пола пользователя
     * (на русском языке)
     */
    @Column(name = "name", nullable = false)
    private String name;

    public static class Constants {

        /**
         * Мужской пол
         */
        public final static UUID MALE = UUID.fromString("6b426722-1c17-4900-ab99-f3374a8e3880");

        /**
         * Женский пол
         */
        public final static UUID FEMALE = UUID.fromString("64b6d07b-6198-4c12-976b-9ebb507680cd");
    }
}
