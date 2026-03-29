package com.memorygrove.backend.telegram.entity;

import com.memorygrove.backend.entity.dbo.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность для хранения привязки пользователя к Telegram-чату
 * <p>
 * Позволяет связать аккаунт пользователя в системе с его Telegram-аккаунтом
 * для отправки уведомлений через бота
 * </p>
 * <p>
 * ВАЖНО: Поле chatId может быть null на этапе генерации кода привязки.
 * Оно становится обязательным только после успешной привязки
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "telegram_bindings")
public class TelegramBinding {

    /**
     * Уникальный идентификатор записи о привязке
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь системы, к которому привязан Telegram-аккаунт
     */
    @NotNull(message = "Пользователь обязателен")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * ID чата в Telegram
     * <p>
     * Может быть null на этапе генерации кода привязки.
     * Заполняется только после успешной привязки через бота.
     * Уникален для каждого пользователя Telegram
     * </p>
     */
    @Column(name = "chat_id", unique = true)
    private Long chatId;  // Убрали @NotNull

    /**
     * Username пользователя в Telegram (опционально)
     */
    @Column(name = "telegram_username")
    private String telegramUsername;

    /**
     * Имя пользователя в Telegram
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Фамилия пользователя в Telegram (опционально)
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Флаг, включены ли уведомления через Telegram
     */
    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled = true;

    /**
     * Дата и время привязки
     */
    @CreatedDate
    @Column(name = "bound_at", nullable = false, updatable = false)
    private LocalDateTime boundAt;

    /**
     * Уникальный код для привязки (генерируется временно)
     * Используется для подтверждения связи между аккаунтом и Telegram
     */
    @Column(name = "binding_code", unique = true)
    private String bindingCode;

    /**
     * Срок действия кода привязки
     */
    @Column(name = "code_expires_at")
    private LocalDateTime codeExpiresAt;
}
