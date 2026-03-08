package com.memorygrove.backend.messenger.controller;

import com.memorygrove.backend.messenger.entity.dto.request.AddContactRequestDto;
import com.memorygrove.backend.messenger.entity.dto.request.UpdateContactRequestDto;
import com.memorygrove.backend.messenger.entity.dto.response.ContactResponseDto;
import com.memorygrove.backend.messenger.service.ContactService;
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
 * REST контроллер для управления доверенными контактами
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Добавления новых контактов</li>
 *   <li>Просмотра списка контактов</li>
 *   <li>Обновления настроек контактов</li>
 *   <li>Удаления контактов</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Tag(name = "Контакты", description = "API для управления доверенными контактами")
@SecurityRequirement(name = "Bearer Authentication")
public class ContactController {

    private final ContactService contactService;
    private final JwtUtil jwtUtil;

    /**
     * Добавить новый доверенный контакт
     *
     * @param requestDto DTO с данными для добавления
     * @return добавленный контакт
     */
    @Operation(
            summary = "Добавить контакт",
            description = "Добавляет нового пользователя в список доверенных контактов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Контакт успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или попытка добавить себя"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Контакт уже существует")
    })
    @PostMapping
    public ResponseEntity<ContactResponseDto> addContact(@Valid @RequestBody AddContactRequestDto requestDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на добавление контакта от пользователя {}", userId);

        ContactResponseDto response = contactService.addContact(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получить все активные контакты текущего пользователя
     *
     * @return список контактов
     */
    @Operation(
            summary = "Получить активные контакты",
            description = "Возвращает список всех активных доверенных контактов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping
    public ResponseEntity<List<ContactResponseDto>> getActiveContacts() {
        UUID userId = getCurrentUserId();
        log.info("Запрос активных контактов от пользователя {}", userId);

        List<ContactResponseDto> contacts = contactService.getActiveContacts(userId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Получить все контакты пользователя (включая неактивные)
     *
     * @return список всех контактов
     */
    @Operation(
            summary = "Получить все контакты",
            description = "Возвращает список всех доверенных контактов, включая неактивные"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ContactResponseDto>> getAllContacts() {
        UUID userId = getCurrentUserId();
        log.info("Запрос всех контактов от пользователя {}", userId);

        List<ContactResponseDto> contacts = contactService.getAllContacts(userId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Получить информацию о конкретном контакте
     *
     * @param contactId ID контакта
     * @return информация о контакте
     */
    @Operation(
            summary = "Получить контакт по ID",
            description = "Возвращает информацию о конкретном доверенном контакте"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Контакт найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден")
    })
    @GetMapping("/{contactId}")
    public ResponseEntity<ContactResponseDto> getContact(
            @Parameter(description = "UUID контакта", required = true)
            @PathVariable UUID contactId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос информации о контакте {} от пользователя {}", contactId, userId);

        ContactResponseDto contact = contactService.getContact(userId, contactId);
        return ResponseEntity.ok(contact);
    }

    /**
     * Обновить настройки контакта
     *
     * @param contactId ID контакта
     * @param requestDto новые настройки
     * @return обновлённый контакт
     */
    @Operation(
            summary = "Обновить настройки контакта",
            description = "Изменяет никнейм и настройки уведомлений для контакта"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Настройки обновлены"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден")
    })
    @PatchMapping("/{contactId}")
    public ResponseEntity<ContactResponseDto> updateContact(
            @Parameter(description = "UUID контакта", required = true)
            @PathVariable UUID contactId,
            @Valid @RequestBody UpdateContactRequestDto requestDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление контакта {} от пользователя {}", contactId, userId);

        ContactResponseDto updated = contactService.updateContact(userId, contactId, requestDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удалить контакт
     *
     * @param contactId ID контакта
     * @return пустой ответ
     */
    @Operation(
            summary = "Удалить контакт",
            description = "Удаляет пользователя из списка доверенных контактов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Контакт удалён"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден")
    })
    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(
            @Parameter(description = "UUID контакта", required = true)
            @PathVariable UUID contactId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление контакта {} от пользователя {}", contactId, userId);

        contactService.deleteContact(userId, contactId);
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
