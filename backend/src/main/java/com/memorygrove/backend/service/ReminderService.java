package com.memorygrove.backend.service;

import com.memorygrove.backend.dictionary.ReminderStatus;
import com.memorygrove.backend.entity.dbo.Reminder;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.reminder.request.ReminderCreateRequestDto;
import com.memorygrove.backend.entity.dto.reminder.request.ReminderUpdateRequestDto;
import com.memorygrove.backend.entity.dto.reminder.response.ReminderResponseDto;
import com.memorygrove.backend.repository.ReminderRepository;
import com.memorygrove.backend.repository.ReminderStatusRepository;
import com.memorygrove.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с напоминаниями пользователей
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Создания новых напоминаний</li>
 *   <li>Просмотра напоминаний (по одному, списком, с пагинацией)</li>
 *   <li>Редактирования существующих напоминаний</li>
 *   <li>Изменения статуса напоминаний</li>
 *   <li>Удаления напоминаний</li>
 *   <li>Автоматической обработки просроченных напоминаний</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderStatusRepository reminderStatusRepository;
    private final UserRepository userRepository;

    /**
     * Создание нового напоминания
     *
     * @param userId ID пользователя, создающего напоминание
     * @param createDto DTO с данными для создания
     * @return DTO с созданным напоминанием
     * @throws EntityNotFoundException если пользователь или статус не найден
     */
    public ReminderResponseDto createReminder(UUID userId, ReminderCreateRequestDto createDto) {
        log.info("Создание нового напоминания для пользователя с ID: {}", userId);

        User user = findUserById(userId);
        ReminderStatus pendingStatus = findReminderStatusById(ReminderStatus.Constants.PENDING);

        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setTitle(createDto.getTitle().trim());
        reminder.setDescription(createDto.getDescription() != null ? createDto.getDescription().trim() : null);
        reminder.setRemindAt(createDto.getRemindAt());
        reminder.setStatus(pendingStatus);

        Reminder savedReminder = reminderRepository.save(reminder);
        log.info("✅ Напоминание успешно создано с ID: {}", savedReminder.getId());

        return ReminderResponseDto.fromEntity(savedReminder);
    }

    /**
     * Получение напоминания по ID
     *
     * @param reminderId ID напоминания
     * @param userId ID пользователя (для проверки прав доступа)
     * @return DTO с данными напоминания
     * @throws EntityNotFoundException если напоминание не найдено
     * @throws AccessDeniedException если напоминание принадлежит другому пользователю
     */
    @Transactional(readOnly = true)
    public ReminderResponseDto getReminderById(UUID reminderId, UUID userId) {
        log.info("Получение напоминания с ID: {} для пользователя: {}", reminderId, userId);

        Reminder reminder = findReminderByIdAndUserId(reminderId, userId);
        return ReminderResponseDto.fromEntity(reminder);
    }

    /**
     * Получение всех напоминаний пользователя (с пагинацией)
     *
     * @param userId ID пользователя
     * @param page номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с напоминаниями
     */
    @Transactional(readOnly = true)
    public Page<ReminderResponseDto> getUserRemindersPaginated(UUID userId, int page, int size) {
        log.info("Получение напоминаний пользователя {} (страница {}, размер {})", userId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "remindAt"));
        Page<Reminder> remindersPage = reminderRepository.findByUserIdOrderByRemindAtDesc(userId, pageable);

        return remindersPage.map(ReminderResponseDto::fromEntity);
    }

    /**
     * Получение всех напоминаний пользователя (без пагинации)
     *
     * @param userId ID пользователя
     * @return список всех напоминаний пользователя
     */
    @Transactional(readOnly = true)
    public List<ReminderResponseDto> getAllUserReminders(UUID userId) {
        log.info("Получение всех напоминаний пользователя: {}", userId);

        List<Reminder> reminders = reminderRepository.findByUserIdOrderByRemindAtDesc(userId);
        return reminders.stream()
                .map(ReminderResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Обновление существующего напоминания
     *
     * @param reminderId ID напоминания
     * @param userId ID пользователя (для проверки прав)
     * @param updateDto DTO с новыми данными
     * @return DTO с обновлённым напоминанием
     * @throws EntityNotFoundException если напоминание или статус не найдены
     * @throws AccessDeniedException если напоминание принадлежит другому пользователю
     */
    public ReminderResponseDto updateReminder(UUID reminderId, UUID userId, ReminderUpdateRequestDto updateDto) {
        log.info("Обновление напоминания с ID: {} для пользователя: {}", reminderId, userId);

        Reminder reminder = findReminderByIdAndUserId(reminderId, userId);
        ReminderStatus newStatus = findReminderStatusById(updateDto.getStatusId());

        reminder.setTitle(updateDto.getTitle().trim());
        reminder.setDescription(updateDto.getDescription() != null ? updateDto.getDescription().trim() : null);
        reminder.setRemindAt(updateDto.getRemindAt());
        reminder.setStatus(newStatus);

        Reminder updatedReminder = reminderRepository.save(reminder);
        log.info("✅ Напоминание {} успешно обновлено", reminderId);

        return ReminderResponseDto.fromEntity(updatedReminder);
    }

    /**
     * Обновление только статуса напоминания
     * Используется для отметки о выполнении, отмены и т.д.
     *
     * @param reminderId ID напоминания
     * @param userId ID пользователя (для проверки прав)
     * @param statusId ID нового статуса
     * @return DTO с обновлённым напоминанием
     * @throws EntityNotFoundException если напоминание или статус не найдены
     * @throws AccessDeniedException если напоминание принадлежит другому пользователю
     */
    public ReminderResponseDto updateReminderStatus(UUID reminderId, UUID userId, UUID statusId) {
        log.info("Обновление статуса напоминания {} для пользователя {} на статус {}",
                reminderId, userId, statusId);

        Reminder reminder = findReminderByIdAndUserId(reminderId, userId);
        ReminderStatus newStatus = findReminderStatusById(statusId);

        reminder.setStatus(newStatus);

        Reminder updatedReminder = reminderRepository.save(reminder);
        log.info("✅ Статус напоминания {} успешно обновлён", reminderId);

        return ReminderResponseDto.fromEntity(updatedReminder);
    }

    /**
     * Удаление напоминания
     *
     * @param reminderId ID напоминания
     * @param userId ID пользователя (для проверки прав)
     * @throws EntityNotFoundException если напоминание не найдено
     * @throws AccessDeniedException если напоминание принадлежит другому пользователю
     */
    public void deleteReminder(UUID reminderId, UUID userId) {
        log.info("Удаление напоминания с ID: {} для пользователя: {}", reminderId, userId);

        if (!reminderRepository.existsByIdAndUserId(reminderId, userId)) {
            log.warn("Напоминание {} не найдено или не принадлежит пользователю {}", reminderId, userId);
            throw new EntityNotFoundException(
                    String.format("Напоминание с ID '%s' не найдено или не принадлежит пользователю", reminderId)
            );
        }

        reminderRepository.deleteById(reminderId);
        log.info("✅ Напоминание {} успешно удалено", reminderId);
    }

    /**
     * Получение напоминаний пользователя по статусу
     *
     * @param userId ID пользователя
     * @param statusId ID статуса
     * @return список напоминаний с указанным статусом
     */
    @Transactional(readOnly = true)
    public List<ReminderResponseDto> getRemindersByStatus(UUID userId, UUID statusId) {
        log.info("Получение напоминаний пользователя {} со статусом {}", userId, statusId);

        List<Reminder> reminders = reminderRepository.findByUserIdAndStatusId(userId, statusId);
        return reminders.stream()
                .map(ReminderResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Планировщик для автоматической обработки просроченных напоминаний
     * Запускается каждую минуту и отправляет уведомления по напоминаниям,
     * время которых наступило
     */
    @Scheduled(fixedDelay = 60000) // Каждую минуту
    @Transactional
    public void processDueReminders() {
        log.debug("Запуск обработки просроченных напоминаний");

        LocalDateTime now = LocalDateTime.now();
        List<Reminder> dueReminders = reminderRepository.findDueReminders(
                ReminderStatus.Constants.PENDING, now);

        if (dueReminders.isEmpty()) {
            log.debug("Нет напоминаний для отправки");
            return;
        }

        log.info("Найдено {} напоминаний для отправки", dueReminders.size());

        ReminderStatus sentStatus = findReminderStatusById(ReminderStatus.Constants.SENT);

        for (Reminder reminder : dueReminders) {
            try {
                // Здесь будет отправка уведомления (Telegram/email)
                log.info("🔔 Отправка напоминания {} для пользователя {}",
                        reminder.getId(), reminder.getUser().getId());

                // После отправки меняем статус на SENT
                reminder.setStatus(sentStatus);
                reminderRepository.save(reminder);

                log.debug("Напоминание {} помечено как отправленное", reminder.getId());
            } catch (Exception e) {
                log.error("Ошибка при обработке напоминания {}: {}", reminder.getId(), e.getMessage());
            }
        }

        log.info("✅ Обработка просроченных напоминаний завершена");
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
     * Поиск статуса напоминания по ID
     *
     * @param statusId ID статуса
     * @return сущность статуса
     * @throws EntityNotFoundException если статус не найден
     */
    private ReminderStatus findReminderStatusById(UUID statusId) {
        return reminderStatusRepository.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Статус напоминания с ID '%s' не найден", statusId)
                ));
    }

    /**
     * Поиск напоминания по ID с проверкой принадлежности пользователю
     *
     * @param reminderId ID напоминания
     * @param userId ID пользователя
     * @return сущность напоминания
     * @throws EntityNotFoundException если напоминание не найдено
     * @throws AccessDeniedException если напоминание принадлежит другому пользователю
     */
    private Reminder findReminderByIdAndUserId(UUID reminderId, UUID userId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Напоминание с ID '%s' не найдено", reminderId)
                ));

        if (!reminder.getUser().getId().equals(userId)) {
            log.warn("Попытка доступа к чужому напоминанию. Напоминание: {}, пользователь: {}",
                    reminderId, userId);
            throw new AccessDeniedException("У вас нет прав на доступ к этому напоминанию");
        }

        return reminder;
    }
}
