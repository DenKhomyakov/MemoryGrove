package com.memorygrove.backend.telegram.controller;

import com.memorygrove.backend.telegram.entity.dto.response.TelegramBindingResponseDto;
import com.memorygrove.backend.telegram.entity.dto.response.TelegramCodeResponseDto;
import com.memorygrove.backend.telegram.service.TelegramBindingService;
import com.memorygrove.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * REST контроллер для управления привязкой Telegram аккаунта
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Генерации кода привязки Telegram</li>
 *   <li>Получения статуса привязки</li>
 *   <li>Отключения Telegram от аккаунта</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
@Tag(name = "Telegram", description = "API для привязки Telegram аккаунта")
@SecurityRequirement(name = "Bearer Authentication")
public class TelegramController {

    private final TelegramBindingService telegramBindingService;
    private final JwtUtil jwtUtil;

    /**
     * Генерирует уникальный код для привязки Telegram аккаунта
     * <p>
     * Код действителен в течение 10 минут. Если у пользователя уже есть
     * действующий код, возвращается существующий.
     * </p>
     *
     * @return ResponseEntity с DTO, содержащим сгенерированный код
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    @Operation(
            summary = "Сгенерировать код привязки",
            description = "Генерирует уникальный код для привязки Telegram аккаунта. Код действителен 10 минут."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Код успешно сгенерирован"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping("/generate-code")
    public ResponseEntity<TelegramCodeResponseDto> generateBindingCode() {
        UUID userId = getCurrentUserId();
        log.info("Запрос на генерацию кода привязки от пользователя {}", userId);

        String code = telegramBindingService.generateBindingCode(userId);
        return ResponseEntity.ok(new TelegramCodeResponseDto(code));
    }

    /**
     * Получает текущий статус привязки Telegram для аутентифицированного пользователя
     * <p>
     * Возвращает информацию о том, привязан ли Telegram, данные аккаунта
     * и статус уведомлений.
     * </p>
     *
     * @return ResponseEntity с DTO, содержащим статус привязки
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    @Operation(
            summary = "Получить статус привязки",
            description = "Возвращает информацию о привязке Telegram аккаунта"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/binding-status")
    public ResponseEntity<TelegramBindingResponseDto> getBindingStatus() {
        UUID userId = getCurrentUserId();
        log.info("Запрос статуса привязки от пользователя {}", userId);

        TelegramBindingResponseDto status = telegramBindingService.getBindingStatus(userId);
        return ResponseEntity.ok(status);
    }

    /**
     * Отключает Telegram от аккаунта пользователя
     * <p>
     * Удаляет запись о привязке из базы данных. После этого пользователь
     * перестаёт получать уведомления в Telegram.
     * </p>
     *
     * @return ResponseEntity с пустым телом и статусом 200 OK
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    @Operation(
            summary = "Отключить Telegram",
            description = "Удаляет привязку Telegram аккаунта"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Привязка удалена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @DeleteMapping("/unbind")
    public ResponseEntity<Void> unbindTelegram() {
        UUID userId = getCurrentUserId();
        log.info("Запрос на отключение Telegram от пользователя {}", userId);

        telegramBindingService.unbindTelegram(userId);
        return ResponseEntity.ok().build();
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
