package com.memorygrove.backend.controller;

import com.memorygrove.backend.entity.dto.interest.request.InterestCreateRequestDto;
import com.memorygrove.backend.entity.dto.interest.request.InterestLikeRequestDto;
import com.memorygrove.backend.entity.dto.interest.request.InterestReorderRequestDto;
import com.memorygrove.backend.entity.dto.interest.request.InterestUpdateRequestDto;
import com.memorygrove.backend.entity.dto.interest.response.CategoryStatsDto;
import com.memorygrove.backend.entity.dto.interest.response.InterestResponseDto;
import com.memorygrove.backend.service.InterestService;
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
 * REST контроллер для работы с коллекцией интересов пользователя
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Создания новых интересов (фильмы, книги, музыка и т.д.)</li>
 *   <li>Просмотра списка интересов</li>
 *   <li>Редактирования интересов</li>
 *   <li>Оценки интересов (лайк/дизлайк)</li>
 *   <li>Удаления интересов</li>
 *   <li>Сортировки интересов</li>
 *   <li>Получения статистики по категориям</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Tag(name = "Коллекция интересов", description = "API для работы с интересами пользователя (фильмы, книги, музыка и т.д.)")
@SecurityRequirement(name = "Bearer Authentication")
public class InterestController {

    private final InterestService interestService;
    private final JwtUtil jwtUtil;

    /**
     * Создание нового интереса
     *
     * @param createDto DTO с данными для создания
     * @return созданный интерес с HTTP статусом 201 Created
     */
    @Operation(
            summary = "Создать новый интерес",
            description = "Добавляет новый элемент в коллекцию интересов пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Интерес успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping
    public ResponseEntity<InterestResponseDto> createInterest(@Valid @RequestBody InterestCreateRequestDto createDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на создание интереса от пользователя: {}", userId);

        InterestResponseDto created = interestService.createInterest(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение всех интересов пользователя
     *
     * @return список всех интересов
     */
    @Operation(
            summary = "Получить все интересы",
            description = "Возвращает все интересы текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список интересов получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping
    public ResponseEntity<List<InterestResponseDto>> getUserInterests() {
        UUID userId = getCurrentUserId();
        log.info("Запрос всех интересов пользователя: {}", userId);

        List<InterestResponseDto> interests = interestService.getUserInterests(userId);
        return ResponseEntity.ok(interests);
    }

    /**
     * Получение интересов по категории
     *
     * @param category категория интересов (FILM, BOOK, MUSIC, GAME, SPORT, HOBBY, OTHER)
     * @return список интересов указанной категории
     */
    @Operation(
            summary = "Получить интересы по категории",
            description = "Возвращает интересы пользователя в указанной категории"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список интересов получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<InterestResponseDto>> getInterestsByCategory(
            @Parameter(description = "Категория интересов", example = "FILM", required = true)
            @PathVariable String category) {
        UUID userId = getCurrentUserId();
        log.info("Запрос интересов пользователя {} по категории {}", userId, category);

        List<InterestResponseDto> interests = interestService.getInterestsByCategory(userId, category);
        return ResponseEntity.ok(interests);
    }

    /**
     * Получение интереса по ID
     *
     * @param interestId UUID интереса
     * @return информация об интересе
     */
    @Operation(
            summary = "Получить интерес по ID",
            description = "Возвращает информацию о конкретном интересе"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Интерес найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на доступ"),
            @ApiResponse(responseCode = "404", description = "Интерес не найден")
    })
    @GetMapping("/{interestId}")
    public ResponseEntity<InterestResponseDto> getInterestById(
            @Parameter(description = "UUID интереса", required = true)
            @PathVariable UUID interestId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос интереса {} от пользователя {}", interestId, userId);

        InterestResponseDto interest = interestService.getInterestById(interestId, userId);
        return ResponseEntity.ok(interest);
    }

    /**
     * Обновление интереса
     *
     * @param interestId UUID интереса
     * @param updateDto новые данные
     * @return обновлённый интерес
     */
    @Operation(
            summary = "Обновить интерес",
            description = "Обновляет информацию о существующем интересе"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Интерес обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Интерес не найден")
    })
    @PutMapping("/{interestId}")
    public ResponseEntity<InterestResponseDto> updateInterest(
            @Parameter(description = "UUID интереса", required = true)
            @PathVariable UUID interestId,
            @Valid @RequestBody InterestUpdateRequestDto updateDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление интереса {} от пользователя {}", interestId, userId);

        InterestResponseDto updated = interestService.updateInterest(interestId, userId, updateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Обновление оценки (лайк/дизлайк) интереса
     *
     * @param interestId UUID интереса
     * @param likeDto данные оценки
     * @return обновлённый интерес
     */
    @Operation(
            summary = "Обновить оценку интереса",
            description = "Быстрое обновление только оценки (лайк/дизлайк) без изменения других полей"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Оценка обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение"),
            @ApiResponse(responseCode = "404", description = "Интерес не найден")
    })
    @PatchMapping("/{interestId}/like")
    public ResponseEntity<InterestResponseDto> updateLike(
            @Parameter(description = "UUID интереса", required = true)
            @PathVariable UUID interestId,
            @Valid @RequestBody InterestLikeRequestDto likeDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление оценки интереса {} от пользователя {}", interestId, userId);

        InterestResponseDto updated = interestService.updateLike(interestId, userId, likeDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление интереса
     *
     * @param interestId UUID интереса
     * @return пустой ответ с HTTP статусом 204 No Content
     */
    @Operation(
            summary = "Удалить интерес",
            description = "Удаляет интерес из коллекции"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Интерес удалён"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Интерес не найден")
    })
    @DeleteMapping("/{interestId}")
    public ResponseEntity<Void> deleteInterest(
            @Parameter(description = "UUID интереса", required = true)
            @PathVariable UUID interestId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление интереса {} от пользователя {}", interestId, userId);

        interestService.deleteInterest(interestId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Изменение порядка сортировки интересов
     *
     * @param reorderDto DTO со списком ID в нужном порядке
     * @return пустой ответ с HTTP статусом 200 OK
     */
    @Operation(
            summary = "Изменить порядок сортировки",
            description = "Принимает список ID интересов в желаемом порядке отображения"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Порядок сортировки обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping("/reorder")
    public ResponseEntity<Void> reorderInterests(@Valid @RequestBody InterestReorderRequestDto reorderDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на изменение порядка сортировки интересов от пользователя {}", userId);

        interestService.reorderInterests(userId, reorderDto.getInterestIds());
        return ResponseEntity.ok().build();
    }

    /**
     * Получение статистики по категориям
     *
     * @return статистика по всем категориям интересов
     */
    @Operation(
            summary = "Получить статистику по категориям",
            description = "Возвращает количество элементов и лайков в каждой категории"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика получена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/stats")
    public ResponseEntity<List<CategoryStatsDto>> getCategoryStats() {
        UUID userId = getCurrentUserId();
        log.info("Запрос статистики по категориям от пользователя {}", userId);

        List<CategoryStatsDto> stats = interestService.getCategoryStats(userId);
        return ResponseEntity.ok(stats);
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
