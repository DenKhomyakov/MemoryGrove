package com.memorygrove.backend.controller;

import com.memorygrove.backend.entity.dto.auth.request.LoginRequestDto;
import com.memorygrove.backend.entity.dto.auth.request.RefreshTokenRequestDto;
import com.memorygrove.backend.entity.dto.auth.response.JwtResponseDto;
import com.memorygrove.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для аутентификации и управления JWT токенами
 * Предоставляет REST API для:
 * - Входа в систему и получения JWT токенов
 * - Обновления access токена по refresh токену
 * - Выхода из системы
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для аутентификации и управления токенами")
public class AuthController {

    private final AuthService authService;

    /**
     * Аутентифицирует пользователя и возвращает JWT токены
     * При успешном входе генерируется пара токенов: access и refresh
     *
     * @param dto DTO с учетными данными пользователя (идентификатор и пароль)
     * @return ResponseEntity с JWT токенами и статусом 200 OK
     * @throws IllegalArgumentException если учетные данные неверны
     */
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Вход в систему и получение JWT токенов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        JwtResponseDto response = authService.authenticate(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Обновляет access токен с использованием refresh токена
     * Используется когда access токен истек, но refresh токен еще действителен
     *
     * @param dto DTO с refresh токеном
     * @return ResponseEntity с новой парой токенов и статусом 200 OK
     * @throws IllegalArgumentException если refresh токен недействителен
     */
    @Operation(
            summary = "Обновление токена",
            description = "Обновление access токена с помощью refresh токена"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
            @ApiResponse(responseCode = "400", description = "Недействительный refresh токен")
    })
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto dto) {
        JwtResponseDto response = authService.refreshToken(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Выполняет выход пользователя из системы
     * Очищает контекст безопасности и может добавить токен в черный список
     *
     * @param token JWT токен для выхода из системы
     * @return ResponseEntity с пустым телом и статусом 200 OK
     */
    @Operation(
            summary = "Выход из системы",
            description = "Завершение сессии пользователя"
    )
    @ApiResponse(responseCode = "200", description = "Успешный выход из системы")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
}
