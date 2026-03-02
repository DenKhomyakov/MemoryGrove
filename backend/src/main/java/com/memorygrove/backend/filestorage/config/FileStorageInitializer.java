package com.memorygrove.backend.filestorage.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Инициализатор файлового хранилища при запуске приложения.
 * <p>
 * Создаёт необходимые директории для загрузки файлов и миниатюр,
 * если они ещё не существуют. Проверяет права доступа к директориям.
 * </p>
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class FileStorageInitializer {

    private final FileStorageProperties fileStorageProperties;

    /**
     * Инициализирует директории при запуске приложения.
     * <p>
     * Метод выполняется автоматически после создания бина.
     * Проверяет существование директорий и создаёт их при необходимости.
     * В случае ошибки логирует её и выбрасывает исключение.
     * </p>
     *
     * @throws RuntimeException если не удаётся создать директории
     */
    @PostConstruct
    public void init() {
        try {
            createDirectoryIfNotExists(fileStorageProperties.getUploadDir(), "загрузок");
            createDirectoryIfNotExists(fileStorageProperties.getThumbnailDir(), "миниатюр");

            log.info("✅ Файловое хранилище успешно инициализировано");
            log.debug("Директория загрузок: {}", Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath());
            log.debug("Директория миниатюр: {}", Paths.get(fileStorageProperties.getThumbnailDir()).toAbsolutePath());

        } catch (IOException e) {
            log.error("❌ Критическая ошибка при инициализации файлового хранилища: {}", e.getMessage());
            throw new RuntimeException("Не удалось инициализировать файловое хранилище", e);
        }
    }

    /**
     * Создаёт директорию, если она не существует.
     *
     * @param dirPath путь к директории
     * @param dirType тип директории (для логирования)
     * @throws IOException если не удаётся создать директорию или проверить права
     */
    private void createDirectoryIfNotExists(String dirPath, String dirType) throws IOException {
        Path path = Paths.get(dirPath);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("✅ Создана директория для {}: {}", dirType, path.toAbsolutePath());
        } else {
            log.debug("Директория для {} уже существует: {}", dirType, path.toAbsolutePath());
        }

        // Проверяем права на запись
        if (!Files.isWritable(path)) {
            log.warn("⚠️ Директория {} не доступна для записи: {}", dirType, path.toAbsolutePath());
        }
    }
}
