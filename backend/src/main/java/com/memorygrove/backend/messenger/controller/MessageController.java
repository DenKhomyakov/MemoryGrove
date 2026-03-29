package com.memorygrove.backend.messenger.controller;

import com.memorygrove.backend.messenger.entity.dto.request.SendMessageRequestDto;
import com.memorygrove.backend.messenger.entity.dto.response.MessageResponseDto;
import com.memorygrove.backend.messenger.entity.dto.response.UnreadCountResponseDto;
import com.memorygrove.backend.messenger.service.MessageService;
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
 * REST контроллер для работы с сообщениями
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Отправки сообщений</li>
 *   <li>Просмотра переписки</li>
 *   <li>Получения последних сообщений</li>
 *   <li>Подсчёта непрочитанных</li>
 *   <li>Удаления сообщений</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Сообщения", description = "API для обмена сообщениями с доверенными контактами")
@SecurityRequirement(name = "Bearer Authentication")
public class MessageController {

    private final MessageService messageService;
    private final JwtUtil jwtUtil;

    /**
     * Отправить новое сообщение
     *
     * @param requestDto DTO с данными сообщения
     * @return отправленное сообщение
     */
    @Operation(
            summary = "Отправить сообщение",
            description = "Отправляет текстовое сообщение доверенному контакту"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Сообщение отправлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав для отправки сообщения"),
            @ApiResponse(responseCode = "404", description = "Получатель не найден")
    })
    @PostMapping
    public ResponseEntity<MessageResponseDto> sendMessage(@Valid @RequestBody SendMessageRequestDto requestDto) {
        UUID senderId = getCurrentUserId();
        log.info("Запрос на отправку сообщения от пользователя {}", senderId);

        MessageResponseDto response = messageService.sendMessage(senderId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получить переписку с конкретным контактом
     *
     * @param contactId ID контакта
     * @param page номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с сообщениями
     */
    @Operation(
            summary = "Получить переписку",
            description = "Возвращает историю сообщений с указанным контактом с пагинацией"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Переписка получена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав для просмотра переписки")
    })
    @GetMapping("/conversation/{contactId}")
    public ResponseEntity<Page<MessageResponseDto>> getConversation(
            @Parameter(description = "UUID контакта", required = true)
            @PathVariable UUID contactId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID userId = getCurrentUserId();
        log.info("Запрос переписки с {} от пользователя {}, страница {}, размер {}",
                contactId, userId, page, size);

        Page<MessageResponseDto> conversation = messageService.getConversation(userId, contactId, page, size);
        return ResponseEntity.ok(conversation);
    }

    /**
     * Получить последние сообщения для всех контактов
     *
     * @return список последних сообщений
     */
    @Operation(
            summary = "Получить последние сообщения",
            description = "Возвращает последнее сообщение от каждого контакта"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/last")
    public ResponseEntity<List<MessageResponseDto>> getLastMessages() {
        UUID userId = getCurrentUserId();
        log.info("Запрос последних сообщений для пользователя {}", userId);

        List<MessageResponseDto> messages = messageService.getLastMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Получить количество непрочитанных сообщений
     *
     * @return количество непрочитанных
     */
    @Operation(
            summary = "Получить количество непрочитанных",
            description = "Возвращает общее количество непрочитанных сообщений"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Количество получено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/unread")
    public ResponseEntity<UnreadCountResponseDto> getUnreadCount() {
        UUID userId = getCurrentUserId();
        log.info("Запрос количества непрочитанных сообщений для пользователя {}", userId);

        UnreadCountResponseDto unreadCount = messageService.getUnreadCount(userId);
        return ResponseEntity.ok(unreadCount);
    }

    /**
     * Отметить все сообщения от контакта как прочитанные
     *
     * @param contactId ID контакта
     * @return пустой ответ
     */
    @Operation(
            summary = "Отметить как прочитанные",
            description = "Помечает все сообщения от указанного контакта как прочитанные"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения отмечены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав")
    })
    @PostMapping("/read/{contactId}")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "UUID контакта", required = true)
            @PathVariable UUID contactId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на отметку сообщений от {} как прочитанных пользователем {}", contactId, userId);

        messageService.markMessagesAsRead(userId, contactId);
        return ResponseEntity.ok().build();
    }

    /**
     * Удалить сообщение
     *
     * @param messageId ID сообщения
     * @return пустой ответ
     */
    @Operation(
            summary = "Удалить сообщение",
            description = "Помечает сообщение как удалённое для текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Сообщение удалено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено")
    })
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "UUID сообщения", required = true)
            @PathVariable UUID messageId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление сообщения {} от пользователя {}", messageId, userId);

        messageService.deleteMessage(userId, messageId);
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
