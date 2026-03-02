package com.memorygrove.backend.filestorage.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующему файлу.
 * <p>
 * Используется, когда запрашиваемый файл не найден в файловой системе
 * или запись о нём отсутствует в базе данных.
 * </p>
 */
public class FileNotFoundException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке
     */
    public FileNotFoundException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause   причина исключения
     */
    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
