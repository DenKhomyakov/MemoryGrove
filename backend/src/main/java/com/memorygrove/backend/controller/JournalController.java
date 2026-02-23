package com.memorygrove.backend.controller;

import com.memorygrove.backend.entity.dto.journal.request.JournalEntryCreateRequestDto;
import com.memorygrove.backend.entity.dto.journal.response.JournalEntryResponseDto;
import com.memorygrove.backend.entity.dto.journal.request.JournalEntryUpdateRequestDto;
import com.memorygrove.backend.service.JournalService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для работы с личным дневником
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Создания записей</li>
 *   <li>Просмотра записей (по одной, списком, с пагинацией)</li>
 *   <li>Редактирования записей</li>
 *   <li>Удаления записей</li>
 *   <li>Получения статистики</li>
 * </ul>
 * </p>
 * <p>
 * ID текущего пользователя извлекается из JWT токена с помощью {@link JwtUtil#extractUserId(String)}.
 * Все операции проверяют принадлежность записей текущему пользователю.
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
@Tag(name = "Личный дневник", description = "API для работы с записями личного дневника")
@SecurityRequirement(name = "Bearer Authentication")
public class JournalController {

    private final JournalService journalService;
    private final JwtUtil jwtUtil;

    /**
     * Получает ID текущего аутентифицированного пользователя из JWT токена.
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
     * Извлекает JWT токен из заголовка Authorization.
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

    /**
     * Создание новой записи в дневнике.
     *
     * @param createDto данные для создания записи
     * @return созданная запись с HTTP статусом 201 Created
     */
    @Operation(
            summary = "Создать новую запись",
            description = "Создаёт новую запись в личном дневнике текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Запись успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping("/entries")
    public ResponseEntity<JournalEntryResponseDto> createEntry(
            @Valid @RequestBody JournalEntryCreateRequestDto createDto
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на создание записи от пользователя: {}", userId);

        JournalEntryResponseDto created = journalService.createEntry(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение записи по ID.
     *
     * @param entryId ID записи
     * @return найденная запись
     */
    @Operation(
            summary = "Получить запись по ID",
            description = "Возвращает запись дневника по её идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запись найдена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на доступ к записи"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    @GetMapping("/entries/{entryId}")
    public ResponseEntity<JournalEntryResponseDto> getEntryById(
            @Parameter(description = "UUID записи", required = true)
            @PathVariable UUID entryId
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на получение записи {} от пользователя {}", entryId, userId);

        JournalEntryResponseDto entry = journalService.getEntryById(entryId, userId);
        return ResponseEntity.ok(entry);
    }

    /**
     * Получение всех записей текущего пользователя (с пагинацией).
     *
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 20)
     * @return страница с записями
     */
    @Operation(
            summary = "Получить все записи (с пагинацией)",
            description = "Возвращает записи текущего пользователя с поддержкой пагинации"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Записи получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/entries")
    public ResponseEntity<Page<JournalEntryResponseDto>> getUserEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос записей пользователя {} (страница {}, размер {})", userId, page, size);

        Page<JournalEntryResponseDto> entries = journalService.getUserEntriesPaginated(userId, page, size);
        return ResponseEntity.ok(entries);
    }

    /**
     * Получение всех записей (без пагинации).
     *
     * @return список всех записей пользователя
     */
    @Operation(
            summary = "Получить все записи (без пагинации)",
            description = "Возвращает все записи текущего пользователя в виде списка"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Записи получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/entries/all")
    public ResponseEntity<List<JournalEntryResponseDto>> getAllUserEntries() {
        UUID userId = getCurrentUserId();
        log.info("Запрос всех записей пользователя {}", userId);

        List<JournalEntryResponseDto> entries = journalService.getAllUserEntries(userId);
        return ResponseEntity.ok(entries);
    }

    /**
     * Обновление существующей записи.
     *
     * @param entryId ID записи
     * @param updateDto новые данные
     * @return обновлённая запись
     */
    @Operation(
            summary = "Обновить запись",
            description = "Обновляет существующую запись в дневнике"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запись обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    @PutMapping("/entries/{entryId}")
    public ResponseEntity<JournalEntryResponseDto> updateEntry(
            @Parameter(description = "UUID записи", required = true)
            @PathVariable UUID entryId,
            @Valid @RequestBody JournalEntryUpdateRequestDto updateDto
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление записи {} от пользователя {}", entryId, userId);

        JournalEntryResponseDto updated = journalService.updateEntry(entryId, userId, updateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление записи.
     *
     * @param entryId ID записи
     * @return пустой ответ с HTTP статусом 204 No Content
     */
    @Operation(
            summary = "Удалить запись",
            description = "Удаляет запись из дневника"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Запись удалена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    @DeleteMapping("/entries/{entryId}")
    public ResponseEntity<Void> deleteEntry(
            @Parameter(description = "UUID записи", required = true)
            @PathVariable UUID entryId
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление записи {} от пользователя {}", entryId, userId);

        journalService.deleteEntry(entryId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение статистики дневника.
     *
     * @return статистика записей пользователя
     */
    @Operation(
            summary = "Получить статистику",
            description = "Возвращает статистику по записям дневника"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика получена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/stats")
    public ResponseEntity<JournalService.JournalStats> getJournalStats() {
        UUID userId = getCurrentUserId();
        log.info("Запрос статистики дневника от пользователя {}", userId);

        JournalService.JournalStats stats = journalService.getJournalStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Поиск записей за период.
     *
     * @param startDate начало периода
     * @param endDate конец периода
     * @return список записей за указанный период
     */
    @Operation(
            summary = "Поиск записей за период",
            description = "Возвращает записи, созданные в указанном временном промежутке"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Записи найдены"),
            @ApiResponse(responseCode = "400", description = "Некорректные даты"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/entries/search")
    public ResponseEntity<List<JournalEntryResponseDto>> searchEntriesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        UUID userId = getCurrentUserId();
        log.info("Поиск записей пользователя {} за период с {} по {}", userId, startDate, endDate);

        List<JournalEntryResponseDto> entries = journalService.getEntriesBetweenDates(userId, startDate, endDate);
        return ResponseEntity.ok(entries);
    }
}
