package com.memorygrove.backend.messenger.entity.dbo;

import com.memorygrove.backend.entity.dbo.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность "Сообщение"
 * <p>
 * Представляет собой текстовое сообщение, отправленное от одного пользователя другому.
 * Сообщения могут отправлять только доверенные контакты друг другу.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {

    /**
     * Уникальный идентификатор сообщения
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Отправитель сообщения
     */
    @NotNull(message = "Отправитель обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Получатель сообщения
     */
    @NotNull(message = "Получатель обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * Текст сообщения
     */
    @NotBlank(message = "Текст сообщения не может быть пустым")
    @Size(max = 2000, message = "Длина сообщения не должна превышать 2000 символов")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Дата и время отправки сообщения
     */
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    /**
     * Дата и время прочтения сообщения
     * <p>
     * Если null, значит сообщение ещё не прочитано
     * </p>
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * Флаг доставки сообщения в Telegram
     */
    @Column(name = "is_delivered", nullable = false)
    private boolean isDelivered = false;

    /**
     * Дата и время доставки в Telegram
     */
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    /**
     * Флаг удаления сообщения отправителем
     * <p>
     * Сообщение не удаляется физически, а помечается как удалённое
     * </p>
     */
    @Column(name = "is_deleted_by_sender", nullable = false)
    private boolean isDeletedBySender = false;

    /**
     * Флаг удаления сообщения получателем
     */
    @Column(name = "is_deleted_by_receiver", nullable = false)
    private boolean isDeletedByReceiver = false;

    /**
     * Проверяет, прочитано ли сообщение
     *
     * @return true если прочитано
     */
    @Transient
    public boolean isRead() {
        return readAt != null;
    }

    /**
     * Отметить сообщение как прочитанное
     */
    public void markAsRead() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    /**
     * Отметить сообщение как доставленное в Telegram
     */
    public void markAsDelivered() {
        if (!this.isDelivered) {
            this.isDelivered = true;
            this.deliveredAt = LocalDateTime.now();
        }
    }
}
