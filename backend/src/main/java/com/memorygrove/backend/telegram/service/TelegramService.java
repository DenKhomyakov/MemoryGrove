package com.memorygrove.backend.telegram.service;

import com.memorygrove.backend.telegram.bot.MemoryGroveBot;
import com.memorygrove.backend.telegram.entity.TelegramBinding;
import com.memorygrove.backend.telegram.repository.TelegramBindingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис для отправки уведомлений через Telegram
 * <p>
 * Предоставляет методы для отправки различных типов уведомлений
 * пользователям, привязавшим Telegram аккаунт.
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TelegramService {

    private final MemoryGroveBot memoryGroveBot;
    private final TelegramBindingRepository bindingRepository;

    /**
     * Отправляет уведомление о напоминании пользователю
     *
     * @param userId ID пользователя в системе
     * @param title заголовок напоминания
     * @param description описание напоминания (может быть null)
     * @return true если сообщение отправлено успешно, false если пользователь не привязал Telegram
     */
    @Transactional(readOnly = true)
    public boolean sendReminderNotification(UUID userId, String title, String description) {
        TelegramBinding binding = bindingRepository.findByUserId(userId)
                .orElse(null);

        if (binding == null || !binding.isNotificationsEnabled()) {
            log.debug("Пользователь {} не привязал Telegram или отключил уведомления", userId);
            return false;
        }

        String message = String.format(
                "🔔 *НАПОМИНАНИЕ*\n\n" +
                        "*%s*\n" +
                        "%s",
                title,
                description != null ? description : ""
        );

        memoryGroveBot.sendMessage(binding.getChatId(), message);
        return true;
    }

    /**
     * Отправляет уведомление о новом сообщении от доверенного контакта
     *
     * @param userId ID пользователя-получателя
     * @param senderName имя отправителя
     * @param messageText текст сообщения
     * @return true если сообщение отправлено успешно
     */
    @Transactional(readOnly = true)
    public boolean sendMessageNotification(UUID userId, String senderName, String messageText) {
        log.debug("Отправка уведомления о новом сообщении пользователю {}", userId);

        TelegramBinding binding = bindingRepository.findByUserId(userId)
                .orElse(null);

        if (binding == null || !binding.isNotificationsEnabled()) {
            log.debug("Пользователь {} не привязал Telegram или отключил уведомления", userId);
            return false;
        }

        // Обрезаем длинное сообщение для уведомления
        String preview = messageText.length() > 100
                ? messageText.substring(0, 97) + "..."
                : messageText;

        String message = String.format(
                "💬 *Новое сообщение от %s*\n\n" +
                        "_%s_\n\n" +
                        "🔗 Перейдите в приложение, чтобы ответить",
                senderName,
                preview
        );

        memoryGroveBot.sendMessage(binding.getChatId(), message);
        log.info("✅ Уведомление о сообщении отправлено пользователю {}", userId);
        return true;
    }

    /**
     * Отправляет уведомление о непрочитанных сообщениях (ежедневный дайджест)
     *
     * @param userId ID пользователя
     * @param unreadCount количество непрочитанных сообщений
     * @return true если сообщение отправлено успешно
     */
    @Transactional(readOnly = true)
    public boolean sendUnreadSummary(UUID userId, long unreadCount) {
        TelegramBinding binding = bindingRepository.findByUserId(userId)
                .orElse(null);

        if (binding == null || !binding.isNotificationsEnabled()) {
            return false;
        }

        String message = String.format(
                "📬 *У вас %s непрочитанных %s*\n\n" +
                        "🔗 Перейдите в приложение, чтобы прочитать",
                unreadCount,
                getMessageWord(unreadCount)
        );

        memoryGroveBot.sendMessage(binding.getChatId(), message);
        return true;
    }

    /**
     * Отправляет приветственное сообщение после привязки
     *
     * @param userId ID пользователя в системе
     * @return true если сообщение отправлено успешно
     */
    @Transactional(readOnly = true)
    public boolean sendWelcomeMessage(UUID userId) {
        TelegramBinding binding = bindingRepository.findByUserId(userId)
                .orElse(null);

        if (binding == null || binding.getChatId() == null) {
            return false;
        }

        String message = "🎉 *Добро пожаловать в семью MemoryGrove!*\n\n" +
                "Теперь вы будете получать уведомления о напоминаниях и новых сообщениях. " +
                "Если захотите отключить уведомления, это можно сделать в профиле на сайте.";

        memoryGroveBot.sendMessage(binding.getChatId(), message);
        return true;
    }

    /**
     * Проверяет, привязан ли пользователь к Telegram
     *
     * @param userId ID пользователя
     * @return true если привязан и уведомления включены
     */
    @Transactional(readOnly = true)
    public boolean isUserBound(UUID userId) {
        return bindingRepository.findByUserId(userId)
                .map(TelegramBinding::isNotificationsEnabled)
                .orElse(false);
    }

    /**
     * Вспомогательный метод для склонения слова "сообщение"
     *
     * @param count количество
     * @return правильная форма слова
     */
    private String getMessageWord(long count) {
        if (count % 10 == 1 && count % 100 != 11) {
            return "сообщение";
        } else if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) {
            return "сообщения";
        } else {
            return "сообщений";
        }
    }
}
