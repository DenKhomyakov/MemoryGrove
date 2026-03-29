package com.memorygrove.backend.messenger.service;

import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.messenger.entity.dbo.Message;
import com.memorygrove.backend.messenger.entity.dto.request.SendMessageRequestDto;
import com.memorygrove.backend.messenger.entity.dto.response.MessageResponseDto;
import com.memorygrove.backend.messenger.entity.dto.response.UnreadCountResponseDto;
import com.memorygrove.backend.messenger.repository.MessageRepository;
import com.memorygrove.backend.repository.UserRepository;
import com.memorygrove.backend.telegram.service.TelegramService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с сообщениями
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Отправки сообщений</li>
 *   <li>Получения истории переписки</li>
 *   <li>Отметки о прочтении</li>
 *   <li>Удаления сообщений</li>
 *   <li>Дублирования в Telegram</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ContactService contactService;
    private final TelegramService telegramService;

    /**
     * Отправить новое сообщение
     *
     * @param senderId ID отправителя
     * @param requestDto DTO с данными сообщения
     * @return отправленное сообщение
     * @throws IllegalArgumentException если отправитель и получатель совпадают
     * @throws AccessDeniedException если пользователи не могут общаться
     * @throws EntityNotFoundException если получатель не найден
     */
    public MessageResponseDto sendMessage(UUID senderId, SendMessageRequestDto requestDto) {
        log.info("Отправка сообщения от пользователя {} пользователю {}",
                senderId, requestDto.getReceiverId());

        UUID receiverId = UUID.fromString(requestDto.getReceiverId());

        // Проверяем, не пытается ли пользователь отправить сообщение сам себе
        if (senderId.equals(receiverId)) {
            log.warn("Пользователь {} попытался отправить сообщение сам себе", senderId);
            throw new IllegalArgumentException("Нельзя отправлять сообщения самому себе");
        }

        // Проверяем, могут ли пользователи общаться
        if (!contactService.canCommunicate(senderId, receiverId)) {
            log.warn("Пользователи {} и {} не могут общаться (нет доверенного контакта)",
                    senderId, receiverId);
            throw new AccessDeniedException("Вы не можете отправлять сообщения этому пользователю");
        }

        // Находим отправителя и получателя
        User sender = findUserById(senderId);
        User receiver = findUserById(receiverId);

        // Создаём сообщение
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(requestDto.getContent().trim());
        message.setSentAt(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);
        log.info("✅ Сообщение {} успешно отправлено", savedMessage.getId());

        // Временно отключаем отправку в Telegram
        // sendToTelegramAsync(savedMessage);

        return MessageResponseDto.fromEntity(savedMessage, senderId);
    }

    /**
     * Получить историю переписки с контактом (с пагинацией)
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     * @param page номер страницы
     * @param size размер страницы
     * @return страница с сообщениями
     * @throws AccessDeniedException если пользователи не могут общаться
     */
    @Transactional(readOnly = true)
    public Page<MessageResponseDto> getConversation(UUID userId, UUID contactId, int page, int size) {
        log.info("Получение переписки между {} и {}, страница {}, размер {}",
                userId, contactId, page, size);

        // Проверяем, могут ли пользователи общаться
        if (!contactService.canCommunicate(userId, contactId)) {
            log.warn("Пользователи {} и {} не могут общаться", userId, contactId);
            throw new AccessDeniedException("Вы не можете просматривать переписку с этим пользователем");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));

        Page<Message> messagesPage = messageRepository.findConversation(userId, contactId, pageable);

        // Вызываем асинхронно отметку о прочтении, чтобы не блокировать основной запрос
//        markMessagesAsReadAsync(userId, contactId);

        return messagesPage.map(message -> MessageResponseDto.fromEntity(message, userId));
    }

    /**
     * Асинхронно отмечает сообщения как прочитанные
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markMessagesAsReadAsync(UUID userId, UUID contactId) {
        try {
            log.debug("Асинхронная отметка сообщений от {} как прочитанных пользователем {}", contactId, userId);
            messageRepository.markMessagesAsRead(userId, contactId, LocalDateTime.now());
            log.debug("✅ Сообщения отмечены как прочитанные");
        } catch (Exception e) {
            log.error("❌ Ошибка при отметке сообщений как прочитанных: {}", e.getMessage());
        }
    }

    /**
     * Получить последние сообщения для всех контактов
     *
     * @param userId ID пользователя
     * @return список последних сообщений
     */
    @Transactional(readOnly = true)
    public List<MessageResponseDto> getLastMessages(UUID userId) {
        log.info("Получение последних сообщений для пользователя {}", userId);

        return messageRepository.findLastMessagesForUser(userId)
                .stream()
                .map(message -> MessageResponseDto.fromEntity(message, userId))
                .collect(Collectors.toList());
    }

    /**
     * Получить количество непрочитанных сообщений
     *
     * @param userId ID пользователя
     * @return DTO с количеством непрочитанных
     */
    @Transactional(readOnly = true)
    public UnreadCountResponseDto getUnreadCount(UUID userId) {
        log.info("Получение количества непрочитанных сообщений для пользователя {}", userId);

        long count = messageRepository.countByReceiverIdAndReadAtIsNullAndIsDeletedByReceiverFalse(userId);

        return new UnreadCountResponseDto(count);
    }

    /**
     * Отметить все сообщения от контакта как прочитанные
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     */
    @Transactional
    public void markMessagesAsRead(UUID userId, UUID contactId) {
        log.debug("Отметка сообщений от {} как прочитанных пользователем {}", contactId, userId);
        messageRepository.markMessagesAsRead(userId, contactId, LocalDateTime.now());
    }

    /**
     * Удалить сообщение (пометить как удалённое)
     *
     * @param userId ID пользователя
     * @param messageId ID сообщения
     * @throws EntityNotFoundException если сообщение не найдено
     * @throws AccessDeniedException если пользователь не имеет прав на удаление
     */
    public void deleteMessage(UUID userId, UUID messageId) {
        log.info("Удаление сообщения {} пользователем {}", messageId, userId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Сообщение с ID '%s' не найдено", messageId)
                ));

        // Проверяем, является ли пользователь отправителем или получателем
        if (message.getSender().getId().equals(userId)) {
            message.setDeletedBySender(true);
            log.debug("Сообщение {} помечено как удалённое отправителем", messageId);
        } else if (message.getReceiver().getId().equals(userId)) {
            message.setDeletedByReceiver(true);
            log.debug("Сообщение {} помечено как удалённое получателем", messageId);
        } else {
            log.warn("Пользователь {} попытался удалить чужое сообщение {}", userId, messageId);
            throw new AccessDeniedException("Вы не можете удалить это сообщение");
        }

        messageRepository.save(message);
        log.info("✅ Сообщение {} успешно удалено", messageId);
    }

    /**
     * Асинхронная отправка сообщения в Telegram
     *
     * @param message сообщение для отправки
     */
    @Async
    protected void sendToTelegramAsync(Message message) {
        try {
            log.debug("Асинхронная отправка сообщения {} в Telegram", message.getId());

            // Проверяем, привязан ли Telegram
            boolean sent = telegramService.sendMessageNotification(
                    message.getReceiver().getId(),
                    message.getSender().getFullName(),
                    message.getContent()
            );

            if (sent) {
                message.markAsDelivered();
                messageRepository.save(message);
                log.debug("✅ Сообщение {} доставлено в Telegram", message.getId());
            } else {
                log.debug("ℹ️ Сообщение {} не доставлено в Telegram (пользователь не привязан или отключил уведомления)", message.getId());
            }
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке сообщения {} в Telegram: {}",
                    message.getId(), e.getMessage());
            // НЕ выбрасываем исключение, чтобы не откатывать транзакцию
        }
    }

    /**
     * Найти пользователя по ID
     *
     * @param userId ID пользователя
     * @return сущность пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */
    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с ID '%s' не найден", userId)
                ));
    }
}
