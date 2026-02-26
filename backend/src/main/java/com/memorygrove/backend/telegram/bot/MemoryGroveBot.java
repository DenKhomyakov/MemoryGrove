package com.memorygrove.backend.telegram.bot;

import com.memorygrove.backend.telegram.entity.TelegramBinding;
import com.memorygrove.backend.telegram.repository.TelegramBindingRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.Optional;

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

    private final TelegramBindingRepository bindingRepository;

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

    public MemoryGroveBot(TelegramBindingRepository bindingRepository) {
        this.bindingRepository = bindingRepository;
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
        String text = update.getMessage().getText().trim().toUpperCase();
        String firstName = update.getMessage().getChat().getFirstName();
        String lastName = update.getMessage().getChat().getLastName();
        String username = update.getMessage().getChat().getUserName();

        log.info("📩 Получено сообщение от {} ({}): {}", firstName, chatId, text);

        if ("/start".equals(text)) {
            sendWelcomeMessage(chatId);
            return;
        }

        // Проверяем, является ли сообщение кодом привязки
        if (text.matches("^[A-Z0-9]{6}$")) {
            // Вызываем транзакционный метод для обработки кода
            processBindingCodeInTransaction(chatId, text, firstName, lastName, username);
        } else {
            sendUnknownCommandMessage(chatId);
        }
    }

    /**
     * Транзакционный метод для обработки кода привязки
     * Вынесен в отдельный метод с @Transactional чтобы избежать LazyInitializationException
     *
     * @param chatId ID чата Telegram
     * @param code код привязки
     * @param firstName имя пользователя в Telegram
     * @param lastName фамилия пользователя в Telegram
     * @param username username в Telegram
     */
    @Transactional
    public void processBindingCodeInTransaction(Long chatId, String code, String firstName,
                                                String lastName, String username) {
        log.info("🔑 Попытка привязки с кодом: {} от chatId: {}", code, chatId);

        Optional<TelegramBinding> bindingOpt = bindingRepository.findByBindingCode(code);

        if (bindingOpt.isEmpty()) {
            sendMessage(chatId, "❌ Код не найден. Проверьте правильность ввода.");
            return;
        }

        TelegramBinding binding = bindingOpt.get();

        // Проверяем, не истёк ли код
        if (binding.getCodeExpiresAt() == null ||
                binding.getCodeExpiresAt().isBefore(LocalDateTime.now())) {
            sendMessage(chatId, "❌ Срок действия кода истёк. Сгенерируйте новый код на сайте.");
            return;
        }

        // Проверяем, не привязан ли уже этот чат к другому аккаунту
        if (bindingRepository.existsByChatId(chatId) &&
                !chatId.equals(binding.getChatId())) {
            sendMessage(chatId, "❌ Этот Telegram аккаунт уже привязан к другому пользователю.");
            return;
        }

        // Принудительно загружаем пользователя в рамках транзакции
        String userEmail = binding.getUser().getEmail();

        // Привязываем чат
        binding.setChatId(chatId);
        binding.setFirstName(firstName);
        binding.setLastName(lastName);
        binding.setTelegramUsername(username);
        binding.setBindingCode(null);
        binding.setCodeExpiresAt(null);
        binding.setBoundAt(LocalDateTime.now());
        binding.setNotificationsEnabled(true);

        bindingRepository.save(binding);

        log.info("✅ Telegram успешно привязан! Пользователь: {}, chatId: {}",
                userEmail, chatId);

        String successMessage = String.format(
                "✅ *Успешно!*\n\n" +
                        "Ваш Telegram аккаунт привязан к аккаунту *%s*.\n\n" +
                        "Теперь вы будете получать уведомления о напоминаниях прямо сюда! 🔔",
                userEmail
        );

        sendMessage(chatId, successMessage);
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
