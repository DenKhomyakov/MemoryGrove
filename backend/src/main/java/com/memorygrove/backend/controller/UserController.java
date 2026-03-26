package com.memorygrove.backend.controller;

import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.user.request.UserCreateDto;
import com.memorygrove.backend.entity.dto.user.request.UserDeactivationDto;
import com.memorygrove.backend.entity.dto.user.request.UserPasswordChangeDto;
import com.memorygrove.backend.entity.dto.user.request.UserUpdateDto;
import com.memorygrove.backend.entity.dto.user.response.UserResponseDto;
import com.memorygrove.backend.filestorage.service.FileStorageService;
import com.memorygrove.backend.repository.UserRepository;
import com.memorygrove.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@Log4j2
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для работы с пользователями")
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

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

    /**
     * Загрузка фотографии профиля
     */
    @Operation(
            summary = "Загрузить фото профиля",
            description = "Загружает фотографию профиля пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фото успешно загружено"),
            @ApiResponse(responseCode = "400", description = "Некорректный файл"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping(value = "/{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> uploadProfilePhoto(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Файл изображения", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        log.info("Запрос на загрузку фото профиля для пользователя: {}", userId);

        UUID currentUserId = getCurrentUserId(authentication);
        if (!currentUserId.equals(userId)) {
            log.warn("Пользователь {} попытался изменить фото пользователя {}", currentUserId, userId);
            throw new AccessDeniedException("Вы не можете изменить фото другого пользователя");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (user.getPhotoPath() != null) {
            try {
                fileStorageService.deleteFile(user.getPhotoPath());
                log.debug("Старое фото удалено: {}", user.getPhotoPath());
            } catch (Exception e) {
                log.warn("Не удалось удалить старое фото: {}", e.getMessage());
            }
        }

        FileStorageService.StoredFile storedFile = fileStorageService.storeFile(file, userId);

        user.setPhotoPath(storedFile.getStoragePath());
        User savedUser = userRepository.save(user);

        log.info("✅ Фото профиля загружено для пользователя: {}", userId);

        return ResponseEntity.ok(UserResponseDto.fromEntity(savedUser));
    }

    /**
     * Получение фотографии профиля (ПУБЛИЧНЫЙ ЭНДПОИНТ - доступен без авторизации)
     */
    @Operation(
            summary = "Получить фото профиля",
            description = "Возвращает фотографию профиля пользователя. Доступен без авторизации."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фото получено"),
            @ApiResponse(responseCode = "404", description = "Фото не найдено")
    })
    @GetMapping("/{userId}/photo")
    public ResponseEntity<Resource> getProfilePhoto(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId
    ) {
        log.debug("Запрос фото профиля для пользователя: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (user.getPhotoPath() == null) {
            log.debug("Фото профиля не найдено для пользователя: {}", userId);
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = fileStorageService.loadFileAsResource(user.getPhotoPath());

            String contentType = determineContentType(user.getPhotoPath());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("Cache-Control", "public, max-age=3600")
                    .body(resource);
        } catch (Exception e) {
            log.error("❌ Ошибка загрузки фото профиля для пользователя {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаление фотографии профиля
     */
    @Operation(
            summary = "Удалить фото профиля",
            description = "Удаляет фотографию профиля пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Фото удалено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @DeleteMapping("/{userId}/photo")
    public ResponseEntity<Void> deleteProfilePhoto(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        log.info("Запрос на удаление фото профиля для пользователя: {}", userId);

        UUID currentUserId = getCurrentUserId(authentication);
        if (!currentUserId.equals(userId)) {
            log.warn("Пользователь {} попытался удалить фото пользователя {}", currentUserId, userId);
            throw new AccessDeniedException("Вы не можете удалить фото другого пользователя");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (user.getPhotoPath() != null) {
            try {
                fileStorageService.deleteFile(user.getPhotoPath());
                user.setPhotoPath(null);
                userRepository.save(user);
                log.info("✅ Фото профиля удалено для пользователя: {}", userId);
            } catch (Exception e) {
                log.error("❌ Ошибка при удалении фото: {}", e.getMessage());
                throw new RuntimeException("Не удалось удалить фото", e);
            }
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Определение MIME-типа по расширению файла
     */
    private String determineContentType(String filePath) {
        if (filePath == null) return "image/jpeg";
        String lowerPath = filePath.toLowerCase();
        if (lowerPath.endsWith(".png")) return "image/png";
        if (lowerPath.endsWith(".gif")) return "image/gif";
        if (lowerPath.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    /**
     * Получение ID текущего пользователя из аутентификации
     */
    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return user.getId();
    }
}
