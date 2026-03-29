package com.memorygrove.backend.controller;

import com.memorygrove.backend.entity.dto.reminder.request.ReminderCreateRequestDto;
import com.memorygrove.backend.entity.dto.reminder.request.ReminderStatusUpdateRequestDto;
import com.memorygrove.backend.entity.dto.reminder.request.ReminderUpdateRequestDto;
import com.memorygrove.backend.entity.dto.reminder.response.ReminderResponseDto;
import com.memorygrove.backend.service.ReminderService;
import com.memorygrove.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для работы с напоминаниями
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Создания напоминаний</li>
 *   <li>Просмотра напоминаний (по одному, списком, с пагинацией)</li>
 *   <li>Редактирования напоминаний</li>
 *   <li>Изменения статуса напоминаний</li>
 *   <li>Удаления напоминаний</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Tag(name = "Напоминания", description = "API для работы с напоминаниями")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderController {

    private final ReminderService reminderService;
    private final JwtUtil jwtUtil;

    /**
     * Создание нового напоминания
     *
     * @param createDto данные для создания напоминания
     * @return созданное напоминание с HTTP статусом 201 Created
     */
    @Operation(
            summary = "Создать новое напоминание",
            description = "Создаёт новое напоминание для текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Напоминание успешно создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping
    public ResponseEntity<ReminderResponseDto> createReminder(
            @Valid @RequestBody ReminderCreateRequestDto createDto
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на создание напоминания от пользователя: {}", userId);

        ReminderResponseDto created = reminderService.createReminder(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение напоминания по ID
     *
     * @param reminderId ID напоминания
     * @return найденное напоминание
     */
    @Operation(
            summary = "Получить напоминание по ID",
            description = "Возвращает напоминание по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Напоминание найдено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на доступ к напоминанию"),
            @ApiResponse(responseCode = "404", description = "Напоминание не найдено")
    })
    @GetMapping("/{reminderId}")
    public ResponseEntity<ReminderResponseDto> getReminderById(
            @Parameter(description = "UUID напоминания", required = true)
            @PathVariable UUID reminderId
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на получение напоминания {} от пользователя {}", reminderId, userId);

        ReminderResponseDto reminder = reminderService.getReminderById(reminderId, userId);
        return ResponseEntity.ok(reminder);
    }

    /**
     * Получение всех напоминаний текущего пользователя (с пагинацией)
     *
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 20)
     * @return страница с напоминаниями
     */
    @Operation(
            summary = "Получить все напоминания (с пагинацией)",
            description = "Возвращает напоминания текущего пользователя с поддержкой пагинации"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Напоминания получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping
    public ResponseEntity<Page<ReminderResponseDto>> getUserReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос напоминаний пользователя {} (страница {}, размер {})", userId, page, size);

        Page<ReminderResponseDto> reminders = reminderService.getUserRemindersPaginated(userId, page, size);
        return ResponseEntity.ok(reminders);
    }

    /**
     * Получение всех напоминаний (без пагинации)
     *
     * @return список всех напоминаний пользователя
     */
    @Operation(
            summary = "Получить все напоминания (без пагинации)",
            description = "Возвращает все напоминания текущего пользователя в виде списка"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Напоминания получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ReminderResponseDto>> getAllUserReminders() {
        UUID userId = getCurrentUserId();
        log.info("Запрос всех напоминаний пользователя {}", userId);

        List<ReminderResponseDto> reminders = reminderService.getAllUserReminders(userId);
        return ResponseEntity.ok(reminders);
    }

    /**
     * Получение напоминаний по статусу
     *
     * @param statusId ID статуса
     * @return список напоминаний с указанным статусом
     */
    @Operation(
            summary = "Получить напоминания по статусу",
            description = "Возвращает напоминания текущего пользователя с указанным статусом"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Напоминания получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Статус не найден")
    })
    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<ReminderResponseDto>> getRemindersByStatus(
            @Parameter(description = "UUID статуса", required = true)
            @PathVariable UUID statusId
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос напоминаний пользователя {} со статусом {}", userId, statusId);

        List<ReminderResponseDto> reminders = reminderService.getRemindersByStatus(userId, statusId);
        return ResponseEntity.ok(reminders);
    }

    /**
     * Обновление существующего напоминания
     *
     * @param reminderId ID напоминания
     * @param updateDto новые данные
     * @return обновлённое напоминание
     */
    @Operation(
            summary = "Обновить напоминание",
            description = "Обновляет существующее напоминание"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Напоминание обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Напоминание или статус не найдены")
    })
    @PutMapping("/{reminderId}")
    public ResponseEntity<ReminderResponseDto> updateReminder(
            @Parameter(description = "UUID напоминания", required = true)
            @PathVariable UUID reminderId,
            @Valid @RequestBody ReminderUpdateRequestDto updateDto
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление напоминания {} от пользователя {}", reminderId, userId);

        ReminderResponseDto updated = reminderService.updateReminder(reminderId, userId, updateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Обновление только статуса напоминания
     *
     * @param reminderId ID напоминания
     * @param statusUpdateDto DTO с новым статусом
     * @return обновлённое напоминание
     */
    @Operation(
            summary = "Обновить статус напоминания",
            description = "Изменяет только статус напоминания (выполнено, отменено и т.д.)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение"),
            @ApiResponse(responseCode = "404", description = "Напоминание или статус не найдены")
    })
    @PatchMapping("/{reminderId}/status")
    public ResponseEntity<ReminderResponseDto> updateReminderStatus(
            @Parameter(description = "UUID напоминания", required = true)
            @PathVariable UUID reminderId,
            @Valid @RequestBody ReminderStatusUpdateRequestDto statusUpdateDto
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление статуса напоминания {} от пользователя {}", reminderId, userId);

        ReminderResponseDto updated = reminderService.updateReminderStatus(
                reminderId, userId, statusUpdateDto.getStatusId());
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление напоминания
     *
     * @param reminderId ID напоминания
     * @return пустой ответ с HTTP статусом 204 No Content
     */
    @Operation(
            summary = "Удалить напоминание",
            description = "Удаляет напоминание"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Напоминание удалено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Напоминание не найдено")
    })
    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(
            @Parameter(description = "UUID напоминания", required = true)
            @PathVariable UUID reminderId
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление напоминания {} от пользователя {}", reminderId, userId);

        reminderService.deleteReminder(reminderId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает ID текущего аутентифицированного пользователя из JWT токена
     *
     * @return UUID текущего пользователя
     * @throws IllegalStateException если пользователь не аутентифицирован или токен отсутствует
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            log.error("Попытка доступа без аутентификации");
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = extractJwtFromRequest(request);

        if (token == null) {
            log.error("JWT токен не найден в запросе");
            throw new IllegalStateException("JWT токен не найден");
        }

        UUID userId = jwtUtil.extractUserId(token);
        log.debug("Извлечён userId из токена: {}", userId);

        return userId;
    }

    /**
     * Извлекает JWT токен из заголовка Authorization
     *
     * @param request HTTP запрос
     * @return JWT токен или null, если заголовок отсутствует или имеет неверный формат
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
