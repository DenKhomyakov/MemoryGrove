package com.memorygrove.backend.controller;

import com.memorygrove.backend.dictionary.ExerciseType;
import com.memorygrove.backend.entity.dto.brain.request.ArithmeticChainRequestDto;
import com.memorygrove.backend.entity.dto.brain.response.ArithmeticChainResponseDto;
import com.memorygrove.backend.entity.dto.brain.response.ExerciseStatsResponseDto;
import com.memorygrove.backend.service.BrainTrainerService;
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
 * REST контроллер для работы с упражнениями для тренировки памяти
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Генерации арифметических цепочек</li>
 *   <li>Проверки ответов</li>
 *   <li>Получения статистики по упражнениям</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/brain")
@RequiredArgsConstructor
@Tag(name = "Тренажёр памяти", description = "API для упражнений по тренировке памяти")
@SecurityRequirement(name = "Bearer Authentication")
public class BrainTrainerController {

    private final BrainTrainerService brainTrainerService;
    private final JwtUtil jwtUtil;

    /**
     * Генерирует новую арифметическую цепочку для решения
     *
     * @param length длина цепочки (по умолчанию 3)
     * @return выражение для решения
     */
    @Operation(
            summary = "Сгенерировать арифметическую цепочку",
            description = "Возвращает выражение вида '5 + 3 - 2' и правильный ответ для проверки"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Цепочка сгенерирована"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/arithmetic/generate")
    public ResponseEntity<ArithmeticChainResponseDto> generateArithmeticChain(
            @Parameter(description = "Длина цепочки (количество операций)", example = "3")
            @RequestParam(defaultValue = "3") int length
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на генерацию арифметической цепочки от пользователя {}, длина={}", userId, length);

        ArithmeticChainResponseDto chain = brainTrainerService.generateArithmeticChain(length);
        return ResponseEntity.ok(chain);
    }

    /**
     * Проверяет ответ на арифметическую цепочку
     *
     * @param correctResult правильный результат (из предыдущего запроса)
     * @param requestDto DTO с ответом пользователя
     * @return true если ответ верный
     */
    @Operation(
            summary = "Проверить ответ",
            description = "Проверяет, правильный ли ответ дал пользователь, и обновляет статистику"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответ проверен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping("/arithmetic/check/{correctResult}")
    public ResponseEntity<Boolean> checkArithmeticAnswer(
            @Parameter(description = "Правильный результат", required = true)
            @PathVariable int correctResult,
            @Valid @RequestBody ArithmeticChainRequestDto requestDto
    ) {
        UUID userId = getCurrentUserId();
        log.info("Проверка ответа на арифметическую цепочку от пользователя {}", userId);

        boolean isCorrect = brainTrainerService.checkArithmeticAnswer(userId, correctResult, requestDto);
        return ResponseEntity.ok(isCorrect);
    }

    /**
     * Получает всю статистику пользователя по упражнениям
     *
     * @return список статистики по каждому упражнению
     */
    @Operation(
            summary = "Получить всю статистику",
            description = "Возвращает статистику по всем упражнениям для текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика получена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/stats")
    public ResponseEntity<List<ExerciseStatsResponseDto>> getAllStats() {
        UUID userId = getCurrentUserId();
        log.info("Запрос всей статистики от пользователя {}", userId);

        List<ExerciseStatsResponseDto> stats = brainTrainerService.getAllStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Получает статистику по конкретному типу упражнения
     *
     * @param exerciseType тип упражнения
     * @return статистика по упражнению
     */
    @Operation(
            summary = "Получить статистику по типу",
            description = "Возвращает статистику по конкретному типу упражнения"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика получена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/stats/{exerciseType}")
    public ResponseEntity<ExerciseStatsResponseDto> getStatsByType(
            @Parameter(description = "Тип упражнения", required = true, example = "ARITHMETIC")
            @PathVariable ExerciseType exerciseType
    ) {
        UUID userId = getCurrentUserId();
        log.info("Запрос статистики по типу {} от пользователя {}", exerciseType, userId);

        ExerciseStatsResponseDto stats = brainTrainerService.getStatsByType(userId, exerciseType);
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
