package com.memorygrove.backend.messenger.controller;

import com.memorygrove.backend.messenger.entity.dto.request.MessageTemplateRequestDto;
import com.memorygrove.backend.messenger.entity.dto.response.MessageTemplateResponseDto;
import com.memorygrove.backend.messenger.service.MessageTemplateService;
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
 * REST контроллер для работы с шаблонами сообщений
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Просмотра доступных шаблонов</li>
 *   <li>Создания пользовательских шаблонов</li>
 *   <li>Обновления шаблонов</li>
 *   <li>Удаления шаблонов</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/messages/templates")
@RequiredArgsConstructor
@Tag(name = "Шаблоны сообщений", description = "API для управления шаблонами быстрых ответов")
@SecurityRequirement(name = "Bearer Authentication")
public class MessageTemplateController {

    private final MessageTemplateService templateService;
    private final JwtUtil jwtUtil;

    /**
     * Получить все шаблоны, доступные пользователю
     *
     * @return список шаблонов
     */
    @Operation(
            summary = "Получить шаблоны",
            description = "Возвращает все системные и пользовательские шаблоны сообщений"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Шаблоны получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping
    public ResponseEntity<List<MessageTemplateResponseDto>> getUserTemplates() {
        UUID userId = getCurrentUserId();
        log.info("Запрос шаблонов для пользователя {}", userId);

        List<MessageTemplateResponseDto> templates = templateService.getUserTemplates(userId);
        return ResponseEntity.ok(templates);
    }

    /**
     * Создать новый пользовательский шаблон
     *
     * @param requestDto DTO с данными шаблона
     * @return созданный шаблон
     */
    @Operation(
            summary = "Создать шаблон",
            description = "Создаёт новый пользовательский шаблон сообщения"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Шаблон создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping
    public ResponseEntity<MessageTemplateResponseDto> createTemplate(
            @Valid @RequestBody MessageTemplateRequestDto requestDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на создание шаблона от пользователя {}", userId);

        MessageTemplateResponseDto response = templateService.createTemplate(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Обновить существующий шаблон
     *
     * @param templateId ID шаблона
     * @param requestDto новые данные
     * @return обновлённый шаблон
     */
    @Operation(
            summary = "Обновить шаблон",
            description = "Обновляет существующий пользовательский шаблон"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Шаблон обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Шаблон не найден")
    })
    @PutMapping("/{templateId}")
    public ResponseEntity<MessageTemplateResponseDto> updateTemplate(
            @Parameter(description = "UUID шаблона", required = true)
            @PathVariable UUID templateId,
            @Valid @RequestBody MessageTemplateRequestDto requestDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление шаблона {} от пользователя {}", templateId, userId);

        MessageTemplateResponseDto updated = templateService.updateTemplate(userId, templateId, requestDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удалить шаблон
     *
     * @param templateId ID шаблона
     * @return пустой ответ
     */
    @Operation(
            summary = "Удалить шаблон",
            description = "Удаляет пользовательский шаблон сообщения"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Шаблон удалён"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Шаблон не найден")
    })
    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "UUID шаблона", required = true)
            @PathVariable UUID templateId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление шаблона {} от пользователя {}", templateId, userId);

        templateService.deleteTemplate(userId, templateId);
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
