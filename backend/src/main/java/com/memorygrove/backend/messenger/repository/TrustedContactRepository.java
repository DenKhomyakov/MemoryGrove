package com.memorygrove.backend.messenger.repository;

import com.memorygrove.backend.messenger.entity.dbo.TrustedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с доверенными контактами пользователей
 */
@Repository
public interface TrustedContactRepository extends JpaRepository<TrustedContact, UUID> {

    /**
     * Найти все активные контакты пользователя
     *
     * @param userId ID пользователя
     * @return список доверенных контактов
     */
    List<TrustedContact> findByUserIdAndIsActiveTrueOrderByAddedAtDesc(UUID userId);

    /**
     * Найти все контакты пользователя (включая неактивные)
     *
     * @param userId ID пользователя
     * @return список всех доверенных контактов
     */
    List<TrustedContact> findByUserIdOrderByAddedAtDesc(UUID userId);

    /**
     * Найти конкретный контакт пользователя
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     * @return Optional с записью о контакте
     */
    Optional<TrustedContact> findByUserIdAndContactId(UUID userId, UUID contactId);

    /**
     * Проверить, является ли пользователь контактом для другого пользователя
     *
     * @param userId ID пользователя
     * @param contactId ID потенциального контакта
     * @return true если является активным контактом
     */
    @Query("SELECT COUNT(tc) > 0 FROM TrustedContact tc " +
            "WHERE tc.user.id = :userId AND tc.contact.id = :contactId AND tc.isActive = true")
    boolean isActiveContact(@Param("userId") UUID userId, @Param("contactId") UUID contactId);

    /**
     * Проверить, существует ли уже такая связь
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     * @return true если существует
     */
    boolean existsByUserIdAndContactId(UUID userId, UUID contactId);

    /**
     * Найти все контакты, которые могут получать уведомления
     *
     * @param userId ID пользователя
     * @return список контактов с включенными уведомлениями
     */
    List<TrustedContact> findByUserIdAndCanReceiveNotificationsTrueAndIsActiveTrue(UUID userId);

    /**
     * Удалить все контакты пользователя (при деактивации аккаунта)
     *
     * @param userId ID пользователя
     */
    void deleteByUserId(UUID userId);
}
