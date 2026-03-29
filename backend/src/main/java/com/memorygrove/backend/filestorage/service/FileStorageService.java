package com.memorygrove.backend.filestorage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с файловым хранилищем.
 * <p>
 * Определяет контракт для операций с файлами: сохранение, загрузка,
 * удаление, создание миниатюр. Реализация зависит от конкретной
 * стратегии хранения (локальная файловая система, облачное хранилище и т.д.).
 * </p>
 */
public interface FileStorageService {

    /**
     * Сохраняет загруженный файл в хранилище.
     *
     * @param file   загруженный файл
     * @param userId идентификатор пользователя (для создания уникальной структуры папок)
     * @return объект с информацией о сохранённом файле (путь, имя, размер)
     * @throws RuntimeException если не удалось сохранить файл
     */
    StoredFile storeFile(MultipartFile file, UUID userId);

    /**
     * Загружает файл из хранилища как ресурс.
     *
     * @param filePath путь к файлу относительно корня хранилища
     * @return ресурс для скачивания
     * @throws RuntimeException если файл не найден или не может быть прочитан
     */
    Resource loadFileAsResource(String filePath);

    /**
     * Удаляет файл из хранилища.
     *
     * @param filePath путь к файлу относительно корня хранилища
     * @return true если файл успешно удалён, false если файл не существовал
     * @throws RuntimeException если не удалось удалить файл из-за ошибки доступа
     */
    boolean deleteFile(String filePath);

    /**
     * Проверяет, существует ли файл в хранилище.
     *
     * @param filePath путь к файлу относительно корня хранилища
     * @return true если файл существует
     */
    boolean fileExists(String filePath);

    /**
     * Создаёт миниатюру для изображения.
     *
     * @param originalPath путь к оригинальному файлу
     * @return путь к созданной миниатюре
     * @throws UnsupportedOperationException если формат изображения не поддерживается
     */
    String createThumbnail(String originalPath);

    /**
     * Получает полный путь к файлу в файловой системе.
     *
     * @param filePath относительный путь
     * @return полный путь
     */
    Path getFullPath(String filePath);

    /**
     * Класс для хранения информации о сохранённом файле.
     */
    class StoredFile {
        private final String fileName;
        private final String storagePath;
        private final long fileSize;
        private final String contentType;

        public StoredFile(String fileName, String storagePath, long fileSize, String contentType) {
            this.fileName = fileName;
            this.storagePath = storagePath;
            this.fileSize = fileSize;
            this.contentType = contentType;
        }

        /**
         * @return оригинальное имя файла
         */
        public String getFileName() { return fileName; }

        /**
         * @return путь к файлу в хранилище
         */
        public String getStoragePath() { return storagePath; }

        /**
         * @return размер файла в байтах
         */
        public long getFileSize() { return fileSize; }

        /**
         * @return MIME-тип файла
         */
        public String getContentType() { return contentType; }
    }
}
