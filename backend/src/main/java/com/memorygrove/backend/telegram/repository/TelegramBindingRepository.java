package com.memorygrove.backend.telegram.repository;

import com.memorygrove.backend.telegram.entity.TelegramBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с привязками Telegram-аккаунтов
 */
@Repository
public interface TelegramBindingRepository extends JpaRepository<TelegramBinding, UUID> {

    /**
     * Найти привязку по ID пользователя
     */
    Optional<TelegramBinding> findByUserId(UUID userId);

    /**
     * Найти привязку по ID чата Telegram
     */
    Optional<TelegramBinding> findByChatId(Long chatId);

    /**
     * Найти привязку по коду для привязки
     */
    Optional<TelegramBinding> findByBindingCode(String bindingCode);

    /**
     * Проверить, существует ли привязка для пользователя
     */
    boolean existsByUserId(UUID userId);

    /**
     * Проверить, существует ли привязка для чата
     */
    boolean existsByChatId(Long chatId);

    /**
     * Удалить все просроченные коды привязки
     */
    void deleteByCodeExpiresAtBefore(LocalDateTime now);
}
