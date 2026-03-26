package com.memorygrove.backend.filestorage.service;

import com.memorygrove.backend.filestorage.config.FileStorageProperties;
import com.memorygrove.backend.filestorage.exception.FileNotFoundException;
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
import java.time.LocalDate;
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
            if (file.isEmpty()) {
                throw new FileStorageException("Не удалось сохранить пустой файл");
            }

            String contentType = file.getContentType();
            if (!fileStorageProperties.getAllowedTypes().contains(contentType)) {
                throw new FileStorageException(
                        String.format("Недопустимый тип файла: %s. Разрешены: %s",
                                contentType, fileStorageProperties.getAllowedTypes())
                );
            }

            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID() + extension;

            String userDir = userId.toString();
            String profilesDir = "profiles";

            String relativePath = String.format("%s/%s/%s", userDir, profilesDir, uniqueFileName);

            Path targetLocation = Paths.get(fileStorageProperties.getUploadDir())
                    .resolve(userDir)
                    .resolve(profilesDir)
                    .resolve(uniqueFileName);

            Files.createDirectories(targetLocation.getParent());

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("✅ Файл профиля сохранён: {}", targetLocation.toString());

            return new StoredFile(
                    originalFileName != null ? originalFileName : "unknown",
                    relativePath,
                    file.getSize(),
                    contentType
            );

        } catch (IOException e) {
            log.error("❌ Ошибка при сохранении файла профиля: {}", e.getMessage());
            throw new FileStorageException("Не удалось сохранить файл", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            Path path;
            if (filePath.startsWith("./uploads/") || filePath.startsWith("./thumbnails/")) {
                path = Paths.get(filePath);
            } else {
                String baseDir = filePath.contains("thumb") ?
                        fileStorageProperties.getThumbnailDir() :
                        fileStorageProperties.getUploadDir();
                path = Paths.get(baseDir).resolve(filePath);
            }

            path = path.normalize();

            log.debug("Загрузка файла по пути: {}", path.toString());

            if (!Files.exists(path)) {
                log.error("❌ Файл не существует: {}", path.toString());
                throw new FileStorageException("Файл не найден: " + path.toString());
            }

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
            Path sourcePath = Paths.get(originalPath).toAbsolutePath().normalize();

            if (!Files.exists(sourcePath)) {
                sourcePath = Paths.get(fileStorageProperties.getUploadDir())
                        .resolve(originalPath)
                        .toAbsolutePath()
                        .normalize();
            }

            log.debug("Создание миниатюры для: {}", sourcePath);

            if (!Files.exists(sourcePath)) {
                throw new FileNotFoundException("Исходный файл не найден: " + sourcePath);
            }

            Path uploadRoot = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
            Path relativePath = uploadRoot.relativize(sourcePath);

            Path parentDir = relativePath.getParent();

            Path thumbnailDir = Paths.get(fileStorageProperties.getThumbnailDir())
                    .resolve(parentDir)
                    .toAbsolutePath()
                    .normalize();

            if (!Files.exists(thumbnailDir)) {
                Files.createDirectories(thumbnailDir);
            }

            String fileName = sourcePath.getFileName().toString();
            Path targetPath = thumbnailDir.resolve("thumb_" + fileName);

            Thumbnails.of(sourcePath.toFile())
                    .size(fileStorageProperties.getThumbnailSize(), fileStorageProperties.getThumbnailSize())
                    .outputFormat("jpg")
                    .toFile(targetPath.toFile());

            log.info("✅ Миниатюра создана: {}", targetPath);

            return targetPath.toString().replace("\\", "/");

        } catch (IOException e) {
            log.error("❌ Ошибка при создании миниатюры: {}", e.getMessage());
            throw new FileStorageException("Ошибка при создании миниатюры: " + e.getMessage(), e);
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
}
