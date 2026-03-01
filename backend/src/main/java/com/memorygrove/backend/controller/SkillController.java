package com.memorygrove.backend.controller;

import com.memorygrove.backend.entity.dto.skill.request.SkillCreateRequestDto;
import com.memorygrove.backend.entity.dto.skill.request.SkillReorderRequestDto;
import com.memorygrove.backend.entity.dto.skill.request.SkillToggleRequestDto;
import com.memorygrove.backend.entity.dto.skill.request.SkillUpdateRequestDto;
import com.memorygrove.backend.entity.dto.skill.response.CategorySkillStatsDto;
import com.memorygrove.backend.entity.dto.skill.response.SkillResponseDto;
import com.memorygrove.backend.entity.dto.skill.response.TotalSkillStatsDto;
import com.memorygrove.backend.service.SkillService;
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
 * REST контроллер для работы с анкетой навыков пользователя
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Создания новых навыков</li>
 *   <li>Просмотра списка навыков</li>
 *   <li>Редактирования навыков</li>
 *   <li>Отметки выполнения навыков</li>
 *   <li>Удаления навыков</li>
 *   <li>Сортировки навыков</li>
 *   <li>Получения статистики по навыкам</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Tag(name = "Анкета навыков", description = "API для работы с навыками пользователя")
@SecurityRequirement(name = "Bearer Authentication")
public class SkillController {

    private final SkillService skillService;
    private final JwtUtil jwtUtil;

    /**
     * Создание нового навыка
     *
     * @param createDto DTO с данными для создания
     * @return созданный навык с HTTP статусом 201 Created
     */
    @Operation(
            summary = "Создать новый навык",
            description = "Добавляет новый навык в анкету пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Навык успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping
    public ResponseEntity<SkillResponseDto> createSkill(@Valid @RequestBody SkillCreateRequestDto createDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на создание навыка от пользователя: {}", userId);

        SkillResponseDto created = skillService.createSkill(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение всех навыков пользователя
     *
     * @return список всех навыков
     */
    @Operation(
            summary = "Получить все навыки",
            description = "Возвращает все навыки текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список навыков получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getUserSkills() {
        UUID userId = getCurrentUserId();
        log.info("Запрос всех навыков пользователя: {}", userId);

        List<SkillResponseDto> skills = skillService.getUserSkills(userId);
        return ResponseEntity.ok(skills);
    }

    /**
     * Получение навыков по категории
     *
     * @param category категория навыков
     * @return список навыков указанной категории
     */
    @Operation(
            summary = "Получить навыки по категории",
            description = "Возвращает навыки пользователя в указанной категории"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список навыков получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SkillResponseDto>> getSkillsByCategory(
            @Parameter(description = "Категория навыков", example = "SELF_CARE", required = true)
            @PathVariable String category) {
        UUID userId = getCurrentUserId();
        log.info("Запрос навыков пользователя {} по категории {}", userId, category);

        List<SkillResponseDto> skills = skillService.getSkillsByCategory(userId, category);
        return ResponseEntity.ok(skills);
    }

    /**
     * Получение навыка по ID
     *
     * @param skillId UUID навыка
     * @return информация о навыке
     */
    @Operation(
            summary = "Получить навык по ID",
            description = "Возвращает информацию о конкретном навыке"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Навык найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на доступ"),
            @ApiResponse(responseCode = "404", description = "Навык не найден")
    })
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillResponseDto> getSkillById(
            @Parameter(description = "UUID навыка", required = true)
            @PathVariable UUID skillId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос навыка {} от пользователя {}", skillId, userId);

        SkillResponseDto skill = skillService.getSkillById(skillId, userId);
        return ResponseEntity.ok(skill);
    }

    /**
     * Обновление навыка
     *
     * @param skillId UUID навыка
     * @param updateDto новые данные
     * @return обновлённый навык
     */
    @Operation(
            summary = "Обновить навык",
            description = "Обновляет информацию о существующем навыке"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Навык обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Навык не найден")
    })
    @PutMapping("/{skillId}")
    public ResponseEntity<SkillResponseDto> updateSkill(
            @Parameter(description = "UUID навыка", required = true)
            @PathVariable UUID skillId,
            @Valid @RequestBody SkillUpdateRequestDto updateDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление навыка {} от пользователя {}", skillId, userId);

        SkillResponseDto updated = skillService.updateSkill(skillId, userId, updateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Быстрое переключение статуса навыка (для чек-бокса)
     *
     * @param skillId UUID навыка
     * @param toggleDto данные для переключения
     * @return обновлённый навык
     */
    @Operation(
            summary = "Переключить статус навыка",
            description = "Быстрое переключение статуса выполнения навыка (для чек-бокса)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение"),
            @ApiResponse(responseCode = "404", description = "Навык не найден")
    })
    @PatchMapping("/{skillId}/toggle")
    public ResponseEntity<SkillResponseDto> toggleSkillStatus(
            @Parameter(description = "UUID навыка", required = true)
            @PathVariable UUID skillId,
            @Valid @RequestBody SkillToggleRequestDto toggleDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на переключение статуса навыка {} от пользователя {}", skillId, userId);

        SkillResponseDto updated = skillService.toggleSkillStatus(skillId, userId, toggleDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление навыка
     *
     * @param skillId UUID навыка
     * @return пустой ответ с HTTP статусом 204 No Content
     */
    @Operation(
            summary = "Удалить навык",
            description = "Удаляет навык из анкеты"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Навык удалён"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Навык не найден")
    })
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(
            @Parameter(description = "UUID навыка", required = true)
            @PathVariable UUID skillId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление навыка {} от пользователя {}", skillId, userId);

        skillService.deleteSkill(skillId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Изменение порядка сортировки навыков
     *
     * @param reorderDto DTO со списком ID в нужном порядке
     * @return пустой ответ с HTTP статусом 200 OK
     */
    @Operation(
            summary = "Изменить порядок сортировки",
            description = "Принимает список ID навыков в желаемом порядке отображения"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Порядок сортировки обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping("/reorder")
    public ResponseEntity<Void> reorderSkills(@Valid @RequestBody SkillReorderRequestDto reorderDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на изменение порядка сортировки навыков от пользователя {}", userId);

        skillService.reorderSkills(userId, reorderDto.getSkillIds());
        return ResponseEntity.ok().build();
    }

    /**
     * Получение статистики по категориям
     *
     * @return статистика по всем категориям навыков
     */
    @Operation(
            summary = "Получить статистику по категориям",
            description = "Возвращает количество навыков и освоенных навыков в каждой категории"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика получена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/stats/categories")
    public ResponseEntity<List<CategorySkillStatsDto>> getCategoryStats() {
        UUID userId = getCurrentUserId();
        log.info("Запрос статистики по категориям от пользователя {}", userId);

        List<CategorySkillStatsDto> stats = skillService.getCategoryStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Получение общей статистики
     *
     * @return общая статистика по всем навыкам
     */
    @Operation(
            summary = "Получить общую статистику",
            description = "Возвращает общее количество навыков и процент освоения"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика получена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/stats/total")
    public ResponseEntity<TotalSkillStatsDto> getTotalStats() {
        UUID userId = getCurrentUserId();
        log.info("Запрос общей статистики от пользователя {}", userId);

        TotalSkillStatsDto stats = skillService.getTotalStats(userId);
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
