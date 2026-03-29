package com.memorygrove.backend.dictionary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Справочник статусов напоминаний.
 * <p>
 * Определяет возможные состояния, в которых может находиться напоминание:
 * <ul>
 *   <li>PENDING - ожидает отправки (новое напоминание)</li>
 *   <li>SENT - отправлено пользователю</li>
 *   <li>COMPLETED - отмечено пользователем как выполненное</li>
 *   <li>CANCELLED - отменено пользователем</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "reminder_statuses")
public class ReminderStatus {

    /**
     * Уникальный идентификатор статуса напоминания
     * Соответствует константам из вложенного класса Constants
     */
    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Наименование статуса напоминания на английском языке
     * Используется в коде для идентификации статуса
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Константы с предопределёнными UUID для статусов напоминаний
     * <p>
     * Эти значения должны соответствовать записям в таблице reminder_statuses
     * </p>
     */
    public static class Constants {

        /**
         * PENDING - Ожидает отправки (новое напоминание)
         */
        public final static UUID PENDING = UUID.fromString("6cc6ee82-b293-453b-a5df-94428f5f84ed");

        /**
         * SENT - Отправлено пользователю
         */
        public final static UUID SENT = UUID.fromString("5f699cae-62e2-431b-a460-e9d594938424");

        /**
         * COMPLETED - Выполнено пользователем
         */
        public final static UUID COMPLETED = UUID.fromString("7665777b-e1e9-4be7-a8c5-fb2e7d457b20");

        /**
         * CANCELLED - Отменено пользователем
         */
        public final static UUID CANCELLED = UUID.fromString("e6ebcf79-82ac-4d92-bb35-0ec71a3845cd");
    }
}
