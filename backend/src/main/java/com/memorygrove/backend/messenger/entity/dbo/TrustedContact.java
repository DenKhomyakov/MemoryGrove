package com.memorygrove.backend.messenger.entity.dbo;

import com.memorygrove.backend.entity.dbo.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность "Доверенный контакт"
 * <p>
 * Представляет собой связь между пользователем и его доверенным контактом.
 * Только такие контакты могут общаться друг с другом через систему.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "trusted_contacts")
public class TrustedContact {

    /**
     * Уникальный идентификатор записи о доверенном контакте
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, который добавил контакт (владелец)
     */
    @NotNull(message = "Пользователь обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Доверенный контакт (другой пользователь системы)
     */
    @NotNull(message = "Контакт обязателен")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private User contact;

    /**
     * Как пользователь называет этот контакт
     * <p>
     * Например: "Сын Петя", "Доктор Иванова", "Сосед"
     * </p>
     */
    @Size(max = 100, message = "Длина никнейма не должна превышать 100 символов")
    @Column(name = "nickname")
    private String nickname;

    /**
     * Дата и время добавления контакта
     */
    @CreatedDate
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    /**
     * Флаг активности контакта
     * <p>
     * Можно временно заблокировать общение, не удаляя контакт
     * </p>
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * Может ли контакт отправлять сообщения
     */
    @Column(name = "can_send_messages", nullable = false)
    private boolean canSendMessages = true;

    /**
     * Получать ли уведомления от этого контакта
     */
    @Column(name = "can_receive_notifications", nullable = false)
    private boolean canReceiveNotifications = true;

    /**
     * Получить отображаемое имя контакта
     * <p>
     * Если задан никнейм, возвращает его, иначе ФИО контакта
     * </p>
     *
     * @return отображаемое имя
     */
    @Transient
    public String getDisplayName() {
        if (nickname != null && !nickname.isBlank()) {
            return nickname;
        }
        return contact.getFullName();
    }
}
