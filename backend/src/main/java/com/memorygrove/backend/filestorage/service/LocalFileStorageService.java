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
                    targetLocation.toString().replace('\\', '/'),
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
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("Файл не найден или недоступен для чтения: " + filePath);
            }
        } catch (MalformedURLException e) {
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

            // Создаём путь для миниатюры
            Path thumbnailPath = Paths.get(fileStorageProperties.getThumbnailDir())
                    .resolve(original.getParent().getFileName()) // месяц
                    .resolve(thumbnailName);

            Files.createDirectories(thumbnailPath.getParent());

            // Создаём миниатюру
            Thumbnails.of(original.toFile())
                    .size(fileStorageProperties.getThumbnailSize(), fileStorageProperties.getThumbnailSize())
                    .outputFormat("jpg")
                    .toFile(thumbnailPath.toFile());

            log.info("✅ Миниатюра создана: {}", thumbnailPath.toString());

            return thumbnailPath.toString().replace('\\', '/');

        } catch (IOException e) {
            log.error("❌ Ошибка при создании миниатюры для {}: {}", originalPath, e.getMessage());
            // Не выбрасываем исключение - миниатюра не критична
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path getFullPath(String filePath) {
        return Paths.get(filePath);
    }
}
