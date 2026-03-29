package com.memorygrove.backend.telegram.service;

import com.memorygrove.backend.telegram.entity.TelegramBinding;
import com.memorygrove.backend.telegram.repository.TelegramBindingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Сервис для обработки привязки Telegram аккаунтов
 * Вынесен в отдельный класс для правильной транзакционностью
 * (нюансы работы с Hibernate)
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TelegramBindingProcessor {

    private final TelegramBindingRepository bindingRepository;

    /**
     * Обрабатывает код привязки в рамках транзакции
     *
     * @param chatId ID чата Telegram
     * @param code код привязки
     * @param firstName имя пользователя в Telegram
     * @param lastName фамилия пользователя в Telegram
     * @param username username в Telegram
     * @return сообщение для отправки пользователю
     */
    @Transactional
    public BindingResult processBindingCode(Long chatId, String code, String firstName,
                                            String lastName, String username) {
        log.info("🔑 Обработка кода привязки: {} от chatId: {}", code, chatId);

        Optional<TelegramBinding> bindingOpt = bindingRepository.findByBindingCode(code);

        if (bindingOpt.isEmpty()) {
            return BindingResult.error("❌ Код не найден. Проверьте правильность ввода.");
        }

        TelegramBinding binding = bindingOpt.get();

        // Проверяем, не истёк ли код
        if (binding.getCodeExpiresAt() == null ||
                binding.getCodeExpiresAt().isBefore(LocalDateTime.now())) {
            return BindingResult.error("❌ Срок действия кода истёк. Сгенерируйте новый код на сайте.");
        }

        // Проверяем, не привязан ли уже этот чат к другому аккаунту
        if (bindingRepository.existsByChatId(chatId) &&
                !chatId.equals(binding.getChatId())) {
            return BindingResult.error("❌ Этот Telegram аккаунт уже привязан к другому пользователю.");
        }

        // Принудительно загружаем email пользователя до сохранения
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

        return BindingResult.success(successMessage);
    }

    /**
     * Внутренний класс для результата обработки
     */
    public static class BindingResult {
        private final boolean success;
        private final String message;

        private BindingResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static BindingResult success(String message) {
            return new BindingResult(true, message);
        }

        public static BindingResult error(String message) {
            return new BindingResult(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
