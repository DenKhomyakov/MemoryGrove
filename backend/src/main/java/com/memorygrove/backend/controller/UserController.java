package com.memorygrove.backend.controller;

import com.memorygrove.backend.entity.dto.user.request.UserCreateDto;
import com.memorygrove.backend.entity.dto.user.request.UserDeactivationDto;
import com.memorygrove.backend.entity.dto.user.request.UserPasswordChangeDto;
import com.memorygrove.backend.entity.dto.user.request.UserUpdateDto;
import com.memorygrove.backend.entity.dto.user.response.UserResponseDto;
import com.memorygrove.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Контроллер для работы с пользователями
 * Предоставляет REST API для:
 * - Регистрации новых пользователей
 * - Обновления данных пользователей
 * - Смены пароля
 * - Деактивации / активации аккаунтов
 * - Поиска и получения информации о пользователях
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для работы с пользователями")
public class UserController {

    private final UserService userService;

    /**
     * Регистрация нового пользователя
     */
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создание нового аккаунта пользователя в системе"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для регистрации"),
            @ApiResponse(responseCode = "409", description = "Пользователь с такой электронной почтой или номером телефона уже существует")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserCreateDto dto
    ) {
        UserResponseDto createdUser = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Получение информации о пользователе по ID
     */
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Получение полной информации о пользователе по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getById(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId
    ) {
        UserResponseDto user = userService.getById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Получение информации о пользователе по электронной почте
     */
    @Operation(
            summary = "Получить пользователя по электронной почте",
            description = "Получение полной информации о пользователе по его электронной почте"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getByEmail(
            @Parameter(description = "Электронная почта пользователя", required = true)
            @PathVariable String email
    ) {
        UserResponseDto user = userService.getByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Обновление данных пользователя
     */
    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновление информации о пользователе (ФИО, телефон, адрес и т.д.)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Номер телефона уже занят другим пользователем")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateDto dto
    ) {
        UserResponseDto updatedUser = userService.update(userId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Смена пароля пользователя
     */
    @Operation(
            summary = "Сменить пароль пользователя",
            description = "Смена пароля пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пароль успешно изменён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или пароли не совпадают"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody UserPasswordChangeDto dto
    ) {
        userService.changePassword(userId, dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Деактивация пользователя
     */
    @Operation(
            summary = "Деактивировать пользователя",
            description = "Деактивация аккаунта пользователя с указанием причины"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно деактивирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Пользователь уже деактивирован")
    })
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody UserDeactivationDto dto
    ) {
        userService.deactivate(userId, dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Активация пользователя
     */
    @Operation(
            summary = "Активировать пользователя",
            description = "Активация ранее деактивированного аккаунта пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно активирован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Пользователь уже активен")
    })
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<Void> activate(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId
    ) {
        userService.activate(userId);
        return ResponseEntity.noContent().build();
    }
}
