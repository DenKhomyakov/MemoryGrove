package com.memorygrove.backend.telegram.bot;

import com.memorygrove.backend.telegram.service.TelegramBindingProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс Telegram бота MemoryGrove
 * <p>
 * Обрабатывает входящие сообщения от пользователей:
 * <ul>
 *   <li>/start - приветственное сообщение и инструкция</li>
 *   <li>Код привязки (6 символов) - привязка Telegram к аккаунту</li>
 * </ul>
 * </p>
 */
@Log4j2
@Component
public class MemoryGroveBot extends TelegramLongPollingBot {

    private final TelegramBindingProcessor bindingProcessor;

    /**
     * Имя Telegram бота
     */
    @Value("${telegram.bot.username}")
    private String botUsername;

    /**
     * Токен Telegram бота
     */
    @Value("${telegram.bot.token}")
    private String botToken;

    public MemoryGroveBot(TelegramBindingProcessor bindingProcessor) {
        this.bindingProcessor = bindingProcessor;
    }

    /**
     * Возвращает username бота, указанный в конфигурации
     *
     * @return username бота
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Возвращает токен бота, указанный в конфигурации
     *
     * @return токен бота
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Обрабатывает входящие обновления от Telegram API
     *
     * @param update объект с данными обновления (сообщение, callback и т.д.)
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }
    }

    /**
     * Обрабатывает текстовые сообщения от пользователей
     *
     * @param update объект с данными сообщения
     */
    private void handleMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        String firstName = update.getMessage().getChat().getFirstName();
        String lastName = update.getMessage().getChat().getLastName();
        String username = update.getMessage().getChat().getUserName();

        log.info("📩 Получено сообщение от {} ({}): {}", firstName, chatId, text);

        // Обрабатываем команду /start (регистронезависимо)
        if ("/start".equalsIgnoreCase(text)) {
            sendWelcomeMessage(chatId);
            return;
        }

        // Проверяем, является ли сообщение кодом привязки (только заглавные буквы и цифры, 6 символов)
        String upperText = text.toUpperCase();
        if (upperText.matches("^[A-Z0-9]{6}$")) {
            // Вызываем сервис для обработки кода
            TelegramBindingProcessor.BindingResult result = bindingProcessor.processBindingCode(
                    chatId, upperText, firstName, lastName, username
            );
            sendMessage(chatId, result.getMessage());

            // Если привязка успешна, отправляем дополнительное приветственное сообщение
            if (result.isSuccess()) {
                sendPostBindingWelcomeMessage(chatId, firstName);
            }
        } else {
            sendUnknownCommandMessage(chatId);
        }
    }

    /**
     * Отправляет приветственное сообщение с инструкцией
     *
     * @param chatId ID чата для отправки
     */
    private void sendWelcomeMessage(Long chatId) {
        String message = "🌳 *Добро пожаловать в MemoryGrove!*\n\n" +
                "Я бот-помощник. Чтобы привязать этот чат к вашему аккаунту:\n\n" +
                "1️⃣ Зайдите в свой профиль на сайте\n" +
                "2️⃣ Нажмите 'Привязать Telegram'\n" +
                "3️⃣ Скопируйте полученный код\n" +
                "4️⃣ Отправьте его мне в ответном сообщении\n\n" +
                "Код выглядит так: `A1B2C3` (6 символов)";

        sendMessage(chatId, message);
    }

    /**
     * Отправляет приветственное сообщение после успешной привязки
     *
     * @param chatId ID чата для отправки
     * @param firstName имя пользователя в Telegram
     */
    private void sendPostBindingWelcomeMessage(Long chatId, String firstName) {
        String message = String.format(
                "🎉 *Рады приветствовать вас, %s!*\n\n" +
                        "Чтобы управлять уведомлениями, зайдите в свой профиль на сайте.",
                firstName != null ? firstName : "друг"
        );

        sendMessage(chatId, message);
    }

    /**
     * Отправляет сообщение о неизвестной команде
     *
     * @param chatId ID чата для отправки
     */
    private void sendUnknownCommandMessage(Long chatId) {
        String message = "👋 Я пока понимаю только:\n" +
                "• Команду /start\n" +
                "• Код привязки из 6 символов (например, A1B2C3)";

        sendMessage(chatId, message);
    }

    /**
     * Отправляет сообщение в указанный чат
     *
     * @param chatId ID чата Telegram
     * @param text текст сообщения (поддерживается Markdown)
     */
    public void sendMessage(Long chatId, String text) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .parseMode("Markdown")
                    .build();

            execute(message);
            log.debug("✅ Сообщение отправлено в чат {}", chatId);

        } catch (TelegramApiException e) {
            log.error("❌ Ошибка отправки сообщения в чат {}: {}", chatId, e.getMessage());
        }
    }
}
