package com.memorygrove.backend.filestorage.service;

import com.memorygrove.backend.filestorage.config.FileStorageProperties;
import com.memorygrove.backend.filestorage.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Реализация файлового хранилища на локальной файловой системе.
 * <p>
 * Сохраняет файлы в директории, указанной в настройках. Создаёт структуру папок
 * по годам/месяцам для удобной организации. Поддерживает создание миниатюр
 * с помощью библиотеки Thumbnailator.
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public StoredFile storeFile(MultipartFile file, UUID userId) {
        try {
            // Проверка на пустой файл
            if (file.isEmpty()) {
                throw new FileStorageException("Не удалось сохранить пустой файл");
            }

            // Проверка допустимого типа файла
            String contentType = file.getContentType();
            if (!fileStorageProperties.getAllowedTypes().contains(contentType)) {
                throw new FileStorageException(
                        String.format("Недопустимый тип файла: %s. Разрешены: %s",
                                contentType, fileStorageProperties.getAllowedTypes())
                );
            }

            // Генерация уникального имени файла
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID() + extension;

            // Создание пути для сохранения (user_id/год/месяц/уникальное_имя)
            java.time.LocalDate now = java.time.LocalDate.now();
            String userDir = userId.toString();
            String yearDir = String.valueOf(now.getYear());
            String monthDir = String.format("%02d", now.getMonthValue());

            // Используем Unix-стиль путей (с прямыми слешами)
            String relativePath = String.format("%s/%s/%s/%s", userDir, yearDir, monthDir, uniqueFileName);

            Path targetLocation = Paths.get(fileStorageProperties.getUploadDir())
                    .resolve(userDir)
                    .resolve(yearDir)
                    .resolve(monthDir)
                    .resolve(uniqueFileName);

            // Создание директорий, если их нет
            Files.createDirectories(targetLocation.getParent());

            // Копирование файла
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("✅ Файл сохранён: {}", targetLocation.toString());

            return new StoredFile(
                    originalFileName != null ? originalFileName : "unknown",
                    relativePath,
                    file.getSize(),
                    contentType
            );

        } catch (IOException e) {
            log.error("❌ Ошибка при сохранении файла: {}", e.getMessage());
            throw new FileStorageException("Не удалось сохранить файл", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            // Если путь уже абсолютный или содержит корневую директорию, используем как есть
            Path path;
            if (filePath.startsWith("./uploads/") || filePath.startsWith("./thumbnails/")) {
                // Относительный путь от корня проекта
                path = Paths.get(filePath);
            } else {
                // Иначе считаем, что это путь относительно соответствующей директории
                String baseDir = filePath.contains("thumb") ?
                        fileStorageProperties.getThumbnailDir() :
                        fileStorageProperties.getUploadDir();
                path = Paths.get(baseDir).resolve(filePath);
            }

            // Нормализуем путь (убираем лишние разделители)
            path = path.normalize();

            log.debug("Загрузка файла по пути: {}", path.toString());

            // Проверяем существование файла
            if (!Files.exists(path)) {
                log.error("❌ Файл не существует: {}", path.toString());
                throw new FileStorageException("Файл не найден: " + path.toString());
            }

            // Проверяем права на чтение
            if (!Files.isReadable(path)) {
                log.error("❌ Файл недоступен для чтения: {}", path.toString());
                throw new FileStorageException("Файл недоступен для чтения: " + path.toString());
            }

            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                log.debug("✅ Файл успешно загружен: {}", path.toString());
                return resource;
            } else {
                log.error("❌ Файл не найден или недоступен для чтения: {}", path.toString());
                throw new FileStorageException("Файл не найден или недоступен для чтения: " + path.toString());
            }
        } catch (MalformedURLException e) {
            log.error("❌ Ошибка при формировании URL для файла {}: {}", filePath, e.getMessage());
            throw new FileStorageException("Ошибка при загрузке файла: " + filePath, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.info("✅ Файл удалён: {}", filePath);
            } else {
                log.warn("⚠️ Файл не найден для удаления: {}", filePath);
            }
            return deleted;
        } catch (IOException e) {
            log.error("❌ Ошибка при удалении файла {}: {}", filePath, e.getMessage());
            throw new FileStorageException("Не удалось удалить файл: " + filePath, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createThumbnail(String originalPath) {
        try {
            Path original = Paths.get(originalPath);
            String fileName = original.getFileName().toString();
            String thumbnailName = "thumb_" + fileName;

            // Извлекаем части пути из оригинального файла
            // Путь: uploads/{userId}/{year}/{month}/{fileName}
            Path parentPath = original.getParent();
            String userId = parentPath.getParent().getParent().getFileName().toString();
            String year = parentPath.getParent().getFileName().toString();
            String month = parentPath.getFileName().toString();

            log.debug("Создание миниатюры для файла: userId={}, year={}, month={}, fileName={}",
                    userId, year, month, fileName);

            Path thumbnailPath = Paths.get(fileStorageProperties.getThumbnailDir())
                    .resolve(userId)
                    .resolve(year)
                    .resolve(month)
                    .resolve(thumbnailName);

            Files.createDirectories(thumbnailPath.getParent());

            // Создаём миниатюру
            Thumbnails.of(original.toFile())
                    .size(fileStorageProperties.getThumbnailSize(), fileStorageProperties.getThumbnailSize())
                    .outputFormat("jpg")
                    .toFile(thumbnailPath.toFile());

            log.info("✅ Миниатюра создана: {}", thumbnailPath.toString());

            if (Files.exists(thumbnailPath)) {
                log.info("✅ Миниатюра успешно создана");
            } else {
                log.error("❌ Миниатюра не создана после операции!");
            }

            return thumbnailPath.toString().replace('\\', '/');

        } catch (IOException e) {
            log.error("❌ Ошибка при создании миниатюры для {}: {}", originalPath, e.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path getFullPath(String filePath) {
        if (filePath.startsWith("./uploads/") || filePath.startsWith("./thumbnails/")) {
            return Paths.get(filePath);
        } else {
            String baseDir = filePath.contains("thumb") ?
                    fileStorageProperties.getThumbnailDir() :
                    fileStorageProperties.getUploadDir();
            return Paths.get(baseDir).resolve(filePath);
        }
    }

    /**
     * Проверяет и конвертирует старый формат пути в новый при необходимости
     */
    private String normalizeThumbnailPath(String thumbnailPath, UUID userId, LocalDateTime uploadDate) {
        if (thumbnailPath == null) return null;

        // Если путь уже в новом формате (содержит userId), возвращаем как есть
        if (thumbnailPath.contains(userId.toString())) {
            return thumbnailPath;
        }

        // Если путь в старом формате (например: ./thumbnails/03/thumb_...)
        if (thumbnailPath.matches(".*/thumbnails/\\d{2}/.*")) {
            String fileName = thumbnailPath.substring(thumbnailPath.lastIndexOf('/') + 1);
            String year = String.valueOf(uploadDate.getYear());
            String month = String.format("%02d", uploadDate.getMonthValue());

            String newPath = String.format("./thumbnails/%s/%s/%s/%s",
                    userId.toString(), year, month, fileName);

            log.info("🔄 Конвертация старого пути {} в новый {}", thumbnailPath, newPath);
            return newPath;
        }

        return thumbnailPath;
    }
}
