package com.memorygrove.backend.filestorage.exception;

/**
 * Исключение, выбрасываемое при попытке загрузить файл недопустимого типа.
 * <p>
 * Возникает, когда пользователь пытается загрузить файл с MIME-типом,
 * не входящим в список разрешённых (например, .exe, .pdf и т.д.).
 * </p>
 */
public class InvalidFileTypeException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке
     */
    public InvalidFileTypeException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause   причина исключения
     */
    public InvalidFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
