package com.memorygrove.backend.controller;

import com.memorygrove.backend.entity.dto.place.request.PlaceCreateRequestDto;
import com.memorygrove.backend.entity.dto.place.request.PlaceReorderRequestDto;
import com.memorygrove.backend.entity.dto.place.request.PlaceUpdateRequestDto;
import com.memorygrove.backend.entity.dto.place.response.PlaceResponseDto;
import com.memorygrove.backend.service.SignificantPlaceService;
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
 * REST контроллер для работы со значимыми местами
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Создания новых мест</li>
 *   <li>Просмотра списка мест</li>
 *   <li>Редактирования мест</li>
 *   <li>Удаления мест</li>
 *   <li>Сортировки мест</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@Tag(name = "Значимые места", description = "API для работы со значимыми местами пользователя")
@SecurityRequirement(name = "Bearer Authentication")
public class SignificantPlaceController {

    private final SignificantPlaceService placeService;
    private final JwtUtil jwtUtil;

    /**
     * Создание нового значимого места
     */
    @Operation(
            summary = "Создать новое место",
            description = "Добавляет новое значимое место для текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Место успешно создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping
    public ResponseEntity<PlaceResponseDto> createPlace(@Valid @RequestBody PlaceCreateRequestDto createDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на создание места от пользователя: {}", userId);

        PlaceResponseDto created = placeService.createPlace(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение всех мест пользователя
     */
    @Operation(
            summary = "Получить все места",
            description = "Возвращает все значимые места текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список мест получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping
    public ResponseEntity<List<PlaceResponseDto>> getUserPlaces() {
        UUID userId = getCurrentUserId();
        log.info("Запрос всех мест пользователя: {}", userId);

        List<PlaceResponseDto> places = placeService.getUserPlaces(userId);
        return ResponseEntity.ok(places);
    }

    /**
     * Получение места по ID
     */
    @Operation(
            summary = "Получить место по ID",
            description = "Возвращает информацию о конкретном месте"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Место найдено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на доступ"),
            @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponseDto> getPlaceById(
            @Parameter(description = "UUID места", required = true)
            @PathVariable UUID placeId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос места {} от пользователя {}", placeId, userId);

        PlaceResponseDto place = placeService.getPlaceById(placeId, userId);
        return ResponseEntity.ok(place);
    }

    /**
     * Обновление места
     */
    @Operation(
            summary = "Обновить место",
            description = "Обновляет информацию о существующем месте"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Место обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @PutMapping("/{placeId}")
    public ResponseEntity<PlaceResponseDto> updatePlace(
            @Parameter(description = "UUID места", required = true)
            @PathVariable UUID placeId,
            @Valid @RequestBody PlaceUpdateRequestDto updateDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление места {} от пользователя {}", placeId, userId);

        PlaceResponseDto updated = placeService.updatePlace(placeId, userId, updateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление места
     */
    @Operation(
            summary = "Удалить место",
            description = "Удаляет значимое место"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Место удалено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> deletePlace(
            @Parameter(description = "UUID места", required = true)
            @PathVariable UUID placeId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление места {} от пользователя {}", placeId, userId);

        placeService.deletePlace(placeId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Изменение порядка сортировки мест
     * <p>
     * Принимает список ID мест в желаемом порядке отображения.
     * Первый элемент списка получит sortOrder = 1, второй - sortOrder = 2 и т.д.
     * </p>
     *
     * @param reorderDto DTO со списком ID мест в нужном порядке
     * @return пустой ответ с HTTP статусом 200 OK
     */
    @Operation(
            summary = "Изменить порядок сортировки мест",
            description = "Принимает список ID мест в желаемом порядке отображения. Первый элемент получит sortOrder = 1 и т.д."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Порядок сортировки обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение")
    })
    @PostMapping("/reorder")
    public ResponseEntity<Void> reorderPlaces(@Valid @RequestBody PlaceReorderRequestDto reorderDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на изменение порядка сортировки мест от пользователя {}", userId);

        placeService.reorderPlaces(userId, reorderDto.getPlaceIds());
        return ResponseEntity.ok().build();
    }

    /**
     * Получает ID текущего аутентифицированного пользователя из JWT токена
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

        return jwtUtil.extractUserId(token);
    }

    /**
     * Извлекает JWT токен из заголовка Authorization
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
