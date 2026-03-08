package com.memorygrove.backend.messenger.repository;

import com.memorygrove.backend.messenger.entity.dbo.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сообщениями
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Найти переписку между двумя пользователями с пагинацией
     *
     * @param user1Id ID первого пользователя
     * @param user2Id ID второго пользователя
     * @param pageable параметры пагинации
     * @return страница с сообщениями
     */
    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id AND m.isDeletedBySender = false) " +
            "   OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id AND m.isDeletedByReceiver = false) " +
            "ORDER BY m.sentAt DESC")
    Page<Message> findConversation(@Param("user1Id") UUID user1Id,
                                   @Param("user2Id") UUID user2Id,
                                   Pageable pageable);

    /**
     * Найти последние сообщения для каждого контакта пользователя
     *
     * @param userId ID пользователя
     * @return список последних сообщений
     */
    @Query("SELECT DISTINCT m FROM Message m " +
            "WHERE m.receiver.id = :userId AND m.isDeletedByReceiver = false " +
            "   AND m.sentAt = (SELECT MAX(m2.sentAt) FROM Message m2 " +
            "                   WHERE m2.sender.id = m.sender.id AND m2.receiver.id = :userId) " +
            "ORDER BY m.sentAt DESC")
    List<Message> findLastMessagesForUser(@Param("userId") UUID userId);

    /**
     * Найти все непрочитанные сообщения пользователя
     *
     * @param userId ID пользователя
     * @return список непрочитанных сообщений
     */
    List<Message> findByReceiverIdAndReadAtIsNullAndIsDeletedByReceiverFalse(UUID userId);

    /**
     * Подсчитать количество непрочитанных сообщений пользователя
     *
     * @param userId ID пользователя
     * @return количество непрочитанных сообщений
     */
    long countByReceiverIdAndReadAtIsNullAndIsDeletedByReceiverFalse(UUID userId);

    /**
     * Найти все сообщения, которые нужно продублировать в Telegram
     *
     * @param receiverId ID получателя
     * @return список сообщений для отправки
     */
    List<Message> findByReceiverIdAndIsDeliveredFalseAndIsDeletedByReceiverFalse(UUID receiverId);

    /**
     * Отметить все сообщения с пользователем как прочитанные
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     * @param readTime время прочтения
     */
    @Query("UPDATE Message m SET m.readAt = :readTime " +
            "WHERE m.receiver.id = :userId AND m.sender.id = :contactId AND m.readAt IS NULL")
    void markMessagesAsRead(@Param("userId") UUID userId,
                            @Param("contactId") UUID contactId,
                            @Param("readTime") LocalDateTime readTime);

    /**
     * Найти сообщения за период
     *
     * @param userId ID пользователя
     * @param startDate начало периода
     * @param endDate конец периода
     * @return список сообщений
     */
    List<Message> findByReceiverIdAndSentAtBetweenOrderBySentAtDesc(
            UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Удалить все сообщения пользователя (при деактивации аккаунта)
     *
     * @param userId ID пользователя
     */
    @Query("DELETE FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
