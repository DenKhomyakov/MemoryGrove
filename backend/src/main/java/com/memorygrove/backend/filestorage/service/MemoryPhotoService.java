package com.memorygrove.backend.filestorage.service;

import com.memorygrove.backend.filestorage.entity.dbo.MemoryPhoto;
import com.memorygrove.backend.filestorage.entity.dto.request.PhotoUpdateMetadataRequestDto;
import com.memorygrove.backend.filestorage.entity.dto.request.PhotoUploadRequestDto;
import com.memorygrove.backend.filestorage.entity.dto.response.PhotoListResponseDto;
import com.memorygrove.backend.filestorage.entity.dto.response.PhotoResponseDto;
import com.memorygrove.backend.filestorage.exception.FileStorageException;
import com.memorygrove.backend.filestorage.repository.MemoryPhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с фотографиями пользователей.
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Загрузки новых фотографий</li>
 *   <li>Просмотра списка фотографий</li>
 *   <li>Редактирования метаданных</li>
 *   <li>Скачивания фотографий</li>
 *   <li>Удаления фотографий</li>
 *   <li>Получения статистики</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MemoryPhotoService {

    private final MemoryPhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    /**
     * Загружает новую фотографию.
     *
     * @param userId    ID пользователя
     * @param requestDto DTO с данными для загрузки
     * @return DTO с информацией о загруженной фотографии
     */
    public PhotoResponseDto uploadPhoto(UUID userId, PhotoUploadRequestDto requestDto) {
        log.info("Загрузка новой фотографии для пользователя: {}", userId);

        // Сохраняем файл
        FileStorageService.StoredFile storedFile = fileStorageService.storeFile(
                requestDto.getFile(), userId
        );

        // Создаём миниатюру
        String thumbnailPath = fileStorageService.createThumbnail(storedFile.getStoragePath());

        // Создаём запись в БД
        MemoryPhoto photo = new MemoryPhoto();
        photo.setUserId(userId);
        photo.setFileName(storedFile.getFileName());
        photo.setStoragePath(storedFile.getStoragePath());
        photo.setThumbnailPath(thumbnailPath);
        photo.setDescription(requestDto.getDescription());
        photo.setLocation(requestDto.getLocation());
        photo.setYear(requestDto.getYear());
        photo.setPeople(requestDto.getPeople());
        photo.setUploadDate(LocalDateTime.now());
        photo.setFileSize(storedFile.getFileSize());
        photo.setContentType(storedFile.getContentType());

        MemoryPhoto savedPhoto = photoRepository.save(photo);
        log.info("✅ Фотография успешно загружена с ID: {}", savedPhoto.getId());

        return PhotoResponseDto.fromEntity(savedPhoto);
    }

    /**
     * Получает список всех фотографий пользователя.
     *
     * @param userId ID пользователя
     * @return DTO со списком фотографий и статистикой
     */
    @Transactional(readOnly = true)
    public PhotoListResponseDto getUserPhotos(UUID userId) {
        log.info("Получение списка фотографий для пользователя: {}", userId);

        List<MemoryPhoto> photos = photoRepository.findByUserIdOrderByUploadDateDesc(userId);
        List<PhotoResponseDto> photoDtos = photos.stream()
                .map(PhotoResponseDto::fromEntity)
                .collect(Collectors.toList());

        // Получаем статистику
        List<Object[]> stats = photoRepository.getUserPhotoStats(userId);
        long totalCount = 0;
        long totalSize = 0;

        if (!stats.isEmpty() && stats.get(0).length > 0) {
            totalCount = ((Number) stats.get(0)[0]).longValue();
            if (stats.get(0).length > 1 && stats.get(0)[1] != null) {
                totalSize = ((Number) stats.get(0)[1]).longValue();
            }
        }

        // Получаем список годов
        List<Integer> years = photoRepository.findDistinctYearsByUserId(userId);

        PhotoListResponseDto response = new PhotoListResponseDto();
        response.setPhotos(photoDtos);
        response.setTotalCount(totalCount);
        response.setTotalSize(totalSize);
        response.setYears(years);

        // Форматируем размер
        if (totalSize < 1024) {
            response.setFormattedTotalSize(totalSize + " B");
        } else {
            double kb = totalSize / 1024.0;
            if (kb < 1024) {
                response.setFormattedTotalSize(String.format("%.1f KB", kb));
            } else {
                double mb = kb / 1024.0;
                response.setFormattedTotalSize(String.format("%.1f MB", mb));
            }
        }

        return response;
    }

    /**
     * Получает фотографию по ID.
     *
     * @param photoId ID фотографии
     * @param userId  ID пользователя
     * @return DTO с данными фотографии
     * @throws EntityNotFoundException если фотография не найдена
     * @throws AccessDeniedException   если фотография принадлежит другому пользователю
     */
    @Transactional(readOnly = true)
    public PhotoResponseDto getPhotoById(UUID photoId, UUID userId) {
        log.info("Получение фотографии {} для пользователя {}", photoId, userId);

        MemoryPhoto photo = findPhotoByIdAndUserId(photoId, userId);
        return PhotoResponseDto.fromEntity(photo);
    }

    /**
     * Обновляет метаданные фотографии.
     *
     * @param photoId   ID фотографии
     * @param userId    ID пользователя
     * @param requestDto новые метаданные
     * @return обновлённая фотография
     */
    public PhotoResponseDto updatePhotoMetadata(UUID photoId, UUID userId,
                                                PhotoUpdateMetadataRequestDto requestDto) {
        log.info("Обновление метаданных фотографии {} для пользователя {}", photoId, userId);

        MemoryPhoto photo = findPhotoByIdAndUserId(photoId, userId);

        if (requestDto.getDescription() != null) {
            photo.setDescription(requestDto.getDescription());
        }
        if (requestDto.getLocation() != null) {
            photo.setLocation(requestDto.getLocation());
        }
        if (requestDto.getYear() != null) {
            photo.setYear(requestDto.getYear());
        }
        if (requestDto.getPeople() != null) {
            photo.setPeople(requestDto.getPeople());
        }

        MemoryPhoto updatedPhoto = photoRepository.save(photo);
        log.info("✅ Метаданные фотографии {} обновлены", photoId);

        return PhotoResponseDto.fromEntity(updatedPhoto);
    }

    /**
     * Скачивает оригинальный файл фотографии.
     *
     * @param photoId ID фотографии
     * @param userId  ID пользователя
     * @return ресурс для скачивания
     */
    @Transactional(readOnly = true)
    public Resource downloadPhoto(UUID photoId, UUID userId) {
        log.info("Скачивание фотографии {} для пользователя {}", photoId, userId);

        MemoryPhoto photo = findPhotoByIdAndUserId(photoId, userId);
        return fileStorageService.loadFileAsResource(photo.getStoragePath());
    }

    /**
     * Получает миниатюру фотографии
     *
     * @param photoId ID фотографии
     * @param userId  ID пользователя
     * @return ресурс с миниатюрой
     */
    @Transactional(readOnly = true)
    public Resource getThumbnail(UUID photoId, UUID userId) {
        log.info("Получение миниатюры фотографии {} для пользователя {}", photoId, userId);

        MemoryPhoto photo = findPhotoByIdAndUserId(photoId, userId);

        if (photo.getThumbnailPath() == null) {
            log.error("❌ Миниатюра для фотографии {} не найдена в БД", photoId);
            throw new EntityNotFoundException("Миниатюра для данной фотографии не найдена");
        }

        // Проверяем существование файла перед загрузкой
        if (!fileStorageService.fileExists(photo.getThumbnailPath())) {
            log.error("❌ Файл миниатюры не существует на диске: {}", photo.getThumbnailPath());

            // Пробуем создать миниатюру заново
            try {
                log.info("🔄 Попытка пересоздать миниатюру для фотографии {}", photoId);
                String newThumbnailPath = fileStorageService.createThumbnail(photo.getStoragePath());

                if (newThumbnailPath != null) {
                    photo.setThumbnailPath(newThumbnailPath);
                    MemoryPhoto updatedPhoto = photoRepository.save(photo);
                    log.info("✅ Миниатюра пересоздана: {}", newThumbnailPath);

                    // Даем файловой системе время на синхронизацию
                    Thread.sleep(100);

                    // Проверяем существование файла после создания
                    if (fileStorageService.fileExists(newThumbnailPath)) {
                        log.info("✅ Файл миниатюры подтверждён на диске");
                        return fileStorageService.loadFileAsResource(newThumbnailPath);
                    } else {
                        log.error("❌ Файл миниатюры не найден сразу после создания");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("❌ Прервано ожидание создания миниатюры: {}", e.getMessage());
            } catch (Exception e) {
                log.error("❌ Не удалось пересоздать миниатюру: {}", e.getMessage());
            }

            throw new FileStorageException("Файл миниатюры не найден: " + photo.getThumbnailPath());
        }

        return fileStorageService.loadFileAsResource(photo.getThumbnailPath());
    }

    /**
     * Удаляет фотографию.
     *
     * @param photoId ID фотографии
     * @param userId  ID пользователя
     */
    public void deletePhoto(UUID photoId, UUID userId) {
        log.info("Удаление фотографии {} для пользователя {}", photoId, userId);

        MemoryPhoto photo = findPhotoByIdAndUserId(photoId, userId);

        // Удаляем файлы
        fileStorageService.deleteFile(photo.getStoragePath());
        if (photo.getThumbnailPath() != null) {
            fileStorageService.deleteFile(photo.getThumbnailPath());
        }

        // Удаляем запись из БД
        photoRepository.delete(photo);
        log.info("✅ Фотография {} успешно удалена", photoId);
    }

    /**
     * Находит фотографию по ID с проверкой принадлежности пользователю.
     *
     * @param photoId ID фотографии
     * @param userId  ID пользователя
     * @return сущность фотографии
     * @throws EntityNotFoundException если фотография не найдена
     * @throws AccessDeniedException   если фотография принадлежит другому пользователю
     */
    private MemoryPhoto findPhotoByIdAndUserId(UUID photoId, UUID userId) {
        MemoryPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Фотография с ID '%s' не найдена", photoId)
                ));

        if (!photo.getUserId().equals(userId)) {
            log.warn("Попытка доступа к чужой фотографии. Фото: {}, пользователь: {}",
                    photoId, userId);
            throw new AccessDeniedException("У вас нет прав на доступ к этой фотографии");
        }

        return photo;
    }

    /**
     * Скачивает оригинальный файл фотографии по ID.
     * Используется для прямого отображения в img src.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadPhotoAsResource(UUID photoId) {
        MemoryPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Фотография не найдена"));

        Resource resource = fileStorageService.loadFileAsResource(photo.getStoragePath());

        String contentType = photo.getContentType();
        if (contentType == null) contentType = "image/jpeg";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + photo.getFileName() + "\"")
                .body(resource);
    }

    /**
     * Получает миниатюру по ID.
     * Используется для прямого отображения в img src.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadThumbnailAsResource(UUID photoId) {
        MemoryPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Фотография не найдена"));

        if (photo.getThumbnailPath() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = fileStorageService.loadFileAsResource(photo.getThumbnailPath());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
