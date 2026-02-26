package com.memorygrove.backend.telegram.service;

import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.repository.UserRepository;
import com.memorygrove.backend.telegram.entity.dto.response.TelegramBindingResponseDto;
import com.memorygrove.backend.telegram.entity.TelegramBinding;
import com.memorygrove.backend.telegram.repository.TelegramBindingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис для управления привязкой Telegram аккаунтов
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Генерации кодов привязки</li>
 *   <li>Проверки статуса привязки</li>
 *   <li>Отключения Telegram от аккаунта</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class TelegramBindingService {

    private final TelegramBindingRepository bindingRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Длина генерируемого кода привязки (в символах)
     */
    private static final int CODE_LENGTH = 6;

    /**
     * Срок действия кода привязки (в минутах)
     */
    private static final int CODE_EXPIRY_MINUTES = 10;

    /**
     * Генерирует уникальный код для привязки Telegram
     * <p>
     * Если у пользователя уже есть действующий код, возвращает его.
     * В противном случае генерирует новый код сроком действия 10 минут
     * </p>
     *
     * @param userId ID пользователя, запросившего код
     * @return сгенерированный код привязки
     * @throws EntityNotFoundException если пользователь не найден
     * @throws IllegalStateException если Telegram уже привязан
     */
    public String generateBindingCode(UUID userId) {
        log.info("Генерация кода привязки для пользователя {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с ID '%s' не найден", userId)
                ));

        // Проверяем, существует ли уже привязка
        TelegramBinding binding = bindingRepository.findByUserId(userId)
                .orElse(new TelegramBinding());

        // Если пользователь уже привязал Telegram, не даём генерировать код
        if (binding.getChatId() != null) {
            log.warn("Пользователь {} уже привязал Telegram", userId);
            throw new IllegalStateException("Telegram уже привязан к этому аккаунту");
        }

        // Если есть действующий код, возвращаем его
        if (binding.getBindingCode() != null &&
                binding.getCodeExpiresAt() != null &&
                binding.getCodeExpiresAt().isAfter(LocalDateTime.now())) {
            log.debug("Возвращаем существующий код для пользователя {}", userId);
            return binding.getBindingCode();
        }

        // Генерируем новый код
        String code = generateRandomCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);

        binding.setUser(user);
        binding.setBindingCode(code);
        binding.setCodeExpiresAt(expiresAt);
        // Не трогаем chatId - он остаётся null

        bindingRepository.save(binding);

        log.info("✅ Код привязки {} сгенерирован для пользователя {}, истекает {}",
                code, userId, expiresAt);
        return code;
    }

    /**
     * Получает статус привязки Telegram для пользователя
     *
     * @param userId ID пользователя
     * @return DTO со статусом привязки
     */
    @Transactional(readOnly = true)
    public TelegramBindingResponseDto getBindingStatus(UUID userId) {
        log.info("Получение статуса привязки для пользователя {}", userId);

        return bindingRepository.findByUserId(userId)
                .map(this::mapToDto)
                .orElse(new TelegramBindingResponseDto(false, null, null, null, null, false));
    }

    /**
     * Отключает Telegram от аккаунта пользователя
     *
     * @param userId ID пользователя
     */
    public void unbindTelegram(UUID userId) {
        log.info("Отключение Telegram для пользователя {}", userId);

        bindingRepository.findByUserId(userId)
                .ifPresent(binding -> {
                    bindingRepository.delete(binding);
                    log.info("✅ Привязка Telegram удалена для пользователя {}", userId);
                });
    }

    /**
     * Генерирует случайный код заданной длины из цифр и заглавных букв
     *
     * @return случайный код
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }

        return code.toString();
    }

    /**
     * Преобразует сущность TelegramBinding в DTO для ответа
     *
     * @param binding сущность привязки
     * @return DTO со статусом привязки
     */
    private TelegramBindingResponseDto mapToDto(TelegramBinding binding) {
        return new TelegramBindingResponseDto(
                binding.getChatId() != null,
                binding.getTelegramUsername(),
                binding.getFirstName(),
                binding.getLastName(),
                binding.getBoundAt(),
                binding.isNotificationsEnabled()
        );
    }
}
