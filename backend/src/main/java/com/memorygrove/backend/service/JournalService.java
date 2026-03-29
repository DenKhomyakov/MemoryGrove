package com.memorygrove.backend.service;

import com.memorygrove.backend.entity.dbo.JournalEntry;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.journal.request.JournalEntryCreateRequestDto;
import com.memorygrove.backend.entity.dto.journal.response.JournalEntryResponseDto;
import com.memorygrove.backend.entity.dto.journal.request.JournalEntryUpdateRequestDto;
import com.memorygrove.backend.repository.JournalEntryRepository;
import com.memorygrove.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с личным дневником пользователя
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Создания новых записей</li>
 *   <li>Просмотра записей (по одной, списком, с пагинацией)</li>
 *   <li>Редактирования существующих записей</li>
 *   <li>Удаления записей</li>
 *   <li>Получения статистики по записям</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final UserRepository userRepository;

    /**
     * Создание новой записи в дневнике
     *
     * @param userId ID пользователя, создающего запись
     * @param createDto DTO с данными для создания
     * @return DTO с созданной записью
     * @throws EntityNotFoundException если пользователь не найден
     */
    public JournalEntryResponseDto createEntry(UUID userId, JournalEntryCreateRequestDto createDto) {
        log.info("Создание новой записи в дневнике для пользователя с ID: {}", userId);

        User user = findUserById(userId);

        JournalEntry entry = new JournalEntry();
        entry.setUser(user);
        entry.setTitle(createDto.getTitle().trim());
        entry.setContent(createDto.getContent().trim());
        entry.setMood(createDto.getMood() != null ? createDto.getMood().trim() : null);

        JournalEntry savedEntry = journalEntryRepository.save(entry);
        log.info("✅ Запись успешно создана с ID: {}", savedEntry.getId());

        return JournalEntryResponseDto.fromEntity(savedEntry);
    }

    /**
     * Получение записи по ID
     *
     * @param entryId ID записи
     * @param userId ID пользователя (для проверки прав доступа)
     * @return DTO с данными записи
     * @throws EntityNotFoundException если запись не найдена
     * @throws AccessDeniedException если запись принадлежит другому пользователю
     */
    @Transactional(readOnly = true)
    public JournalEntryResponseDto getEntryById(UUID entryId, UUID userId) {
        log.info("Получение записи с ID: {} для пользователя: {}", entryId, userId);

        JournalEntry entry = findEntryByIdAndUserId(entryId, userId);
        return JournalEntryResponseDto.fromEntity(entry);
    }

    /**
     * Получение всех записей пользователя (с пагинацией)
     *
     * @param userId ID пользователя
     * @param page номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с записями
     */
    @Transactional(readOnly = true)
    public Page<JournalEntryResponseDto> getUserEntriesPaginated(UUID userId, int page, int size) {
        log.info("Получение записей пользователя {} (страница {}, размер {})", userId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JournalEntry> entriesPage = journalEntryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return entriesPage.map(JournalEntryResponseDto::fromEntity);
    }

    /**
     * Получение всех записей пользователя (без пагинации)
     *
     * @param userId ID пользователя
     * @return список всех записей пользователя
     */
    @Transactional(readOnly = true)
    public List<JournalEntryResponseDto> getAllUserEntries(UUID userId) {
        log.info("Получение всех записей пользователя: {}", userId);

        List<JournalEntry> entries = journalEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return entries.stream()
                .map(JournalEntryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Обновление существующей записи
     *
     * @param entryId ID записи
     * @param userId ID пользователя (для проверки прав)
     * @param updateDto DTO с новыми данными
     * @return DTO с обновлённой записью
     * @throws EntityNotFoundException если запись не найдена
     * @throws AccessDeniedException если запись принадлежит другому пользователю
     */
    public JournalEntryResponseDto updateEntry(UUID entryId, UUID userId, JournalEntryUpdateRequestDto updateDto) {
        log.info("Обновление записи с ID: {} для пользователя: {}", entryId, userId);

        JournalEntry entry = findEntryByIdAndUserId(entryId, userId);

        entry.setTitle(updateDto.getTitle().trim());
        entry.setContent(updateDto.getContent().trim());
        entry.setMood(updateDto.getMood() != null ? updateDto.getMood().trim() : null);
        // updatedAt обновится автоматически через @LastModifiedDate

        JournalEntry updatedEntry = journalEntryRepository.save(entry);
        log.info("✅ Запись {} успешно обновлена", entryId);

        return JournalEntryResponseDto.fromEntity(updatedEntry);
    }

    /**
     * Удаление записи
     *
     * @param entryId ID записи
     * @param userId ID пользователя (для проверки прав)
     * @throws EntityNotFoundException если запись не найдена
     * @throws AccessDeniedException если запись принадлежит другому пользователю
     */
    public void deleteEntry(UUID entryId, UUID userId) {
        log.info("Удаление записи с ID: {} для пользователя: {}", entryId, userId);

        if (!journalEntryRepository.existsByIdAndUserId(entryId, userId)) {
            log.warn("Запись {} не найдена или не принадлежит пользователю {}", entryId, userId);
            throw new EntityNotFoundException(
                    String.format("Запись с ID '%s' не найдена или не принадлежит пользователю", entryId)
            );
        }

        journalEntryRepository.deleteById(entryId);
        log.info("✅ Запись {} успешно удалена", entryId);
    }

    /**
     * Получение статистики по записям пользователя
     *
     * @param userId ID пользователя
     * @return массив статистики [total, withMood, edited]
     */
    @Transactional(readOnly = true)
    public JournalStats getJournalStats(UUID userId) {
        log.info("Получение статистики дневника для пользователя: {}", userId);

        List<Object[]> results = journalEntryRepository.getJournalStats(userId);

        if (results == null || results.isEmpty()) {
            return new JournalStats(0L, 0L, 0L);
        }

        Object[] stats = results.get(0);

        long totalEntries = 0L;
        long entriesWithMood = 0L;
        long editedEntries = 0L;

        if (stats.length > 0 && stats[0] != null) {
            totalEntries = ((Number) stats[0]).longValue();
        }

        if (stats.length > 1 && stats[1] != null) {
            entriesWithMood = ((Number) stats[1]).longValue();
        }

        if (stats.length > 2 && stats[2] != null) {
            editedEntries = ((Number) stats[2]).longValue();
        }

        return new JournalStats(totalEntries, entriesWithMood, editedEntries);
    }

    /**
     * Поиск записей за период
     *
     * @param userId ID пользователя
     * @param startDate начало периода
     * @param endDate конец периода
     * @return список записей за период
     */
    @Transactional(readOnly = true)
    public List<JournalEntryResponseDto> getEntriesBetweenDates(
            UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Поиск записей пользователя {} за период с {} по {}", userId, startDate, endDate);

        List<JournalEntry> entries = journalEntryRepository
                .findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startDate, endDate);

        return entries.stream()
                .map(JournalEntryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Поиск пользователя по ID
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

    /**
     * Поиск записи по ID с проверкой принадлежности пользователю
     *
     * @param entryId ID записи
     * @param userId ID пользователя
     * @return сущность записи
     * @throws EntityNotFoundException если запись не найдена
     * @throws AccessDeniedException если запись принадлежит другому пользователю
     */
    private JournalEntry findEntryByIdAndUserId(UUID entryId, UUID userId) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Запись с ID '%s' не найдена", entryId)
                ));

        if (!entry.getUser().getId().equals(userId)) {
            log.warn("Попытка доступа к чужой записи. Запись: {}, пользователь: {}", entryId, userId);
            throw new AccessDeniedException("У вас нет прав на доступ к этой записи");
        }

        return entry;
    }

    /**
     * Внутренний класс для статистики дневника
     */
    @Data
    @AllArgsConstructor
    public static class JournalStats {

        /**
         * Общее количество записей
         */
        private long totalEntries;

        /**
         * Количество записей с указанием настроения
         */
        private long entriesWithMood;

        /**
         * Количество отредактированных записей
         */
        private long editedEntries;
    }
}
