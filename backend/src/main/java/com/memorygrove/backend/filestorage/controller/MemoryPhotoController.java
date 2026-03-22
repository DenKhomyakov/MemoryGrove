package com.memorygrove.backend.filestorage.controller;

import com.memorygrove.backend.filestorage.config.FileStorageProperties;
import com.memorygrove.backend.filestorage.entity.dto.request.PhotoUpdateMetadataRequestDto;
import com.memorygrove.backend.filestorage.entity.dto.request.PhotoUploadRequestDto;
import com.memorygrove.backend.filestorage.entity.dto.response.PhotoListResponseDto;
import com.memorygrove.backend.filestorage.entity.dto.response.PhotoResponseDto;
import com.memorygrove.backend.filestorage.exception.FileSizeLimitExceededException;
import com.memorygrove.backend.filestorage.exception.FileStorageException;
import com.memorygrove.backend.filestorage.exception.InvalidFileTypeException;
import com.memorygrove.backend.filestorage.service.MemoryPhotoService;
import com.memorygrove.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * REST контроллер для работы с фотографиями пользователя.
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Загрузки новых фотографий</li>
 *   <li>Просмотра списка фотографий</li>
 *   <li>Редактирования метаданных</li>
 *   <li>Скачивания оригинальных файлов</li>
 *   <li>Получения миниатюр</li>
 *   <li>Удаления фотографий</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/api/memory/photos")
@RequiredArgsConstructor
@Tag(name = "Цифровая память - Фотографии", description = "API для работы с фотографиями пользователя")
@SecurityRequirement(name = "Bearer Authentication")
public class MemoryPhotoController {

    private final MemoryPhotoService photoService;
    private final FileStorageProperties fileStorageProperties;
    private final JwtUtil jwtUtil;

    /**
     * Загружает новую фотографию.
     *
     * @param file        файл изображения
     * @param description описание (опционально)
     * @param location    место съёмки (опционально)
     * @param year        год съёмки (опционально)
     * @param people      люди на фото (опционально)
     * @return загруженная фотография с HTTP статусом 201 Created
     */
    @Operation(
            summary = "Загрузить фотографию",
            description = "Загружает новую фотографию в личный архив пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Фотография успешно загружена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или неподдерживаемый тип файла"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "413", description = "Превышен допустимый размер файла")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoResponseDto> uploadPhoto(
            @Parameter(description = "Файл изображения", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Описание фотографии")
            @RequestParam(value = "description", required = false) String description,

            @Parameter(description = "Место съёмки")
            @RequestParam(value = "location", required = false) String location,

            @Parameter(description = "Год съёмки")
            @RequestParam(value = "year", required = false) Integer year,

            @Parameter(description = "Люди на фотографии")
            @RequestParam(value = "people", required = false) String people) {

        UUID userId = getCurrentUserId();

        // Проверка размера файла
        if (file.getSize() > fileStorageProperties.getMaxFileSize()) {
            throw new FileSizeLimitExceededException(
                    String.format("Размер файла превышает максимально допустимый (%d MB)",
                            fileStorageProperties.getMaxFileSize() / (1024 * 1024))
            );
        }

        // Проверка типа файла
        String contentType = file.getContentType();
        if (!fileStorageProperties.getAllowedTypes().contains(contentType)) {
            throw new InvalidFileTypeException(
                    String.format("Недопустимый тип файла: %s. Разрешены: %s",
                            contentType, fileStorageProperties.getAllowedTypes())
            );
        }

        log.info("Запрос на загрузку фотографии от пользователя: {}, файл: {}, размер: {} байт",
                userId, file.getOriginalFilename(), file.getSize());

        PhotoUploadRequestDto requestDto = new PhotoUploadRequestDto(
                file, description, location, year, people
        );

        PhotoResponseDto response = photoService.uploadPhoto(userId, requestDto);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Получает список всех фотографий пользователя.
     *
     * @return список фотографий со статистикой
     */
    @Operation(
            summary = "Получить все фотографии",
            description = "Возвращает список всех фотографий пользователя с метаданными и статистикой"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список фотографий получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping
    public ResponseEntity<PhotoListResponseDto> getUserPhotos() {
        UUID userId = getCurrentUserId();
        log.info("Запрос списка фотографий пользователя: {}", userId);

        PhotoListResponseDto response = photoService.getUserPhotos(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Получает фотографию по ID.
     *
     * @param photoId UUID фотографии
     * @return информация о фотографии
     */
    @Operation(
            summary = "Получить фотографию по ID",
            description = "Возвращает метаданные фотографии по её идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фотография найдена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на доступ"),
            @ApiResponse(responseCode = "404", description = "Фотография не найдена")
    })
    @GetMapping("/{photoId}")
    public ResponseEntity<PhotoResponseDto> getPhotoById(
            @Parameter(description = "UUID фотографии", required = true)
            @PathVariable UUID photoId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос фотографии {} от пользователя {}", photoId, userId);

        PhotoResponseDto response = photoService.getPhotoById(photoId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Обновляет метаданные фотографии.
     *
     * @param photoId    UUID фотографии
     * @param requestDto новые метаданные
     * @return обновлённая фотография
     */
    @Operation(
            summary = "Обновить метаданные фотографии",
            description = "Обновляет описание, место, год или список людей на фотографии"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Метаданные обновлены"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Фотография не найдена")
    })
    @PatchMapping("/{photoId}")
    public ResponseEntity<PhotoResponseDto> updatePhotoMetadata(
            @Parameter(description = "UUID фотографии", required = true)
            @PathVariable UUID photoId,
            @Valid @RequestBody PhotoUpdateMetadataRequestDto requestDto) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на обновление метаданных фотографии {} от пользователя {}", photoId, userId);

        PhotoResponseDto response = photoService.updatePhotoMetadata(photoId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Скачивает оригинальный файл фотографии.
     *
     * @param photoId UUID фотографии
     * @return файл для скачивания
     */
    @Operation(
            summary = "Скачать фотографию",
            description = "Скачивает оригинальный файл фотографии"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл отправлен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на доступ"),
            @ApiResponse(responseCode = "404", description = "Фотография не найдена")
    })
    @GetMapping("/{photoId}/download")
    public ResponseEntity<Resource> downloadPhoto(
            @Parameter(description = "UUID фотографии", required = true)
            @PathVariable UUID photoId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на скачивание фотографии {} от пользователя {}", photoId, userId);

        Resource resource = photoService.downloadPhoto(photoId, userId);
        String contentType = determineContentType(resource);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Получает миниатюру фотографии.
     *
     * @param photoId UUID фотографии
     * @return миниатюра для отображения
     */
    @Operation(
            summary = "Получить миниатюру",
            description = "Возвращает миниатюру фотографии для отображения в галерее"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Миниатюра получена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на доступ"),
            @ApiResponse(responseCode = "404", description = "Фотография или миниатюра не найдена")
    })
    @GetMapping("/{photoId}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(
            @Parameter(description = "UUID фотографии", required = true)
            @PathVariable UUID photoId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос миниатюры фотографии {} от пользователя {}", photoId, userId);

        try {
            Resource resource = photoService.getThumbnail(photoId, userId);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (EntityNotFoundException e) {
            log.error("❌ Фотография не найдена: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (FileStorageException e) {
            log.error("❌ Ошибка при загрузке миниатюры: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("❌ Неожиданная ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Удаляет фотографию.
     *
     * @param photoId UUID фотографии
     * @return пустой ответ с HTTP статусом 204 No Content
     */
    @Operation(
            summary = "Удалить фотографию",
            description = "Удаляет фотографию и все связанные с ней файлы"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Фотография удалена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Фотография не найдена")
    })
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @Parameter(description = "UUID фотографии", required = true)
            @PathVariable UUID photoId) {
        UUID userId = getCurrentUserId();
        log.info("Запрос на удаление фотографии {} от пользователя {}", photoId, userId);

        photoService.deletePhoto(photoId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Определяет MIME-тип ресурса по его имени.
     *
     * @param resource ресурс
     * @return MIME-тип
     */
    private String determineContentType(Resource resource) {
        String filename = resource.getFilename();
        if (filename != null) {
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (filename.endsWith(".png")) {
                return "image/png";
            } else if (filename.endsWith(".gif")) {
                return "image/gif";
            } else if (filename.endsWith(".webp")) {
                return "image/webp";
            }
        }
        return "application/octet-stream";
    }

    /**
     * Получает ID текущего аутентифицированного пользователя из JWT токена.
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
     * Извлекает JWT токен из заголовка Authorization.
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
