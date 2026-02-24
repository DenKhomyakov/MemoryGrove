package com.memorygrove.backend.telegram.bot;

import com.memorygrove.backend.telegram.repository.TelegramBindingRepository;
import com.memorygrove.backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс Telegram бота MemoryGrove
 */
@Log4j2
@Component
public class MemoryGroveBot extends TelegramLongPollingBot {

    private final TelegramBindingRepository bindingRepository;
    private final UserRepository userRepository;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    public MemoryGroveBot(
            TelegramBindingRepository bindingRepository,
            UserRepository userRepository) {
        this.bindingRepository = bindingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }
    }

    /**
     * Обрабатывает текстовые сообщения
     */
    private void handleMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String firstName = update.getMessage().getChat().getFirstName();

        log.info("📩 Получено сообщение от {} ({}): {}", firstName, chatId, text);

        if ("/start".equals(text)) {
            sendMessage(chatId,
                    "🌳 *Добро пожаловать в MemoryGrove!*\n\n" +
                            "Я бот-помощник. Чтобы получать уведомления, " +
                            "привяжите этот чат к вашему аккаунту через сайт."
            );
            return;
        }

        sendMessage(chatId,
                "👋 Привет! Я пока понимаю только команду /start"
        );
    }

    /**
     * Отправляет сообщение
     */
    public void sendMessage(Long chatId, String text) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .parseMode("Markdown")
                    .build();

            execute(message);

        } catch (TelegramApiException e) {
            log.error("❌ Ошибка отправки сообщения: {}", e.getMessage());
        }
    }
}
