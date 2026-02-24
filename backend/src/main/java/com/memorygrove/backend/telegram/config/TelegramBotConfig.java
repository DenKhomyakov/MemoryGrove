package com.memorygrove.backend.telegram.config;

import com.memorygrove.backend.telegram.bot.MemoryGroveBot;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Конфигурация Telegram бота
 * <p>
 * Настраивает подключение к Telegram API и регистрирует обработчики
 * </p>
 */
@Log4j2
@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    /**
     * Создаёт и регистрирует бота в системе Telegram
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(MemoryGroveBot memoryGroveBot) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(memoryGroveBot);
            log.info("✅ Telegram бот '{}' успешно зарегистрирован", botUsername);
            return botsApi;
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка регистрации Telegram бота: {}", e.getMessage());
            throw new RuntimeException("Ошибка регистрации Telegram бота", e);
        }
    }
}
