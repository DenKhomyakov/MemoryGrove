package com.memorygrove.backend.filestorage.exception;

/**
 * Исключение, выбрасываемое при ошибках работы с файловым хранилищем.
 * <p>
 * Используется для обработки ошибок сохранения, загрузки и удаления файлов,
 * а также при проблемах с созданием миниатюр или превышении допустимых размеров.
 * </p>
 */
public class FileStorageException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке
     */
    public FileStorageException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause   причина исключения
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
