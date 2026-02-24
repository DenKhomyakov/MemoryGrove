package com.memorygrove.backend.telegram.service;

import com.memorygrove.backend.telegram.bot.MemoryGroveBot;
import com.memorygrove.backend.telegram.entity.TelegramBinding;
import com.memorygrove.backend.telegram.repository.TelegramBindingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.UUID;

/**
 * Сервис для отправки уведомлений через Telegram
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
     * @param description описание напоминания
     * @return true если сообщение отправлено успешно
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

        return sendMessage(binding.getChatId(), message);
    }

    /**
     * Отправляет простое сообщение в указанный чат
     *
     * @param chatId ID чата Telegram
     * @param text текст сообщения
     * @return true если отправлено успешно
     */
    public boolean sendMessage(Long chatId, String text) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .parseMode("Markdown")
                    .build();

            memoryGroveBot.execute(message);
            log.debug("✅ Сообщение отправлено в чат {}", chatId);
            return true;

        } catch (TelegramApiException e) {
            log.error("❌ Ошибка отправки сообщения в чат {}: {}", chatId, e.getMessage());
            return false;
        }
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
}
