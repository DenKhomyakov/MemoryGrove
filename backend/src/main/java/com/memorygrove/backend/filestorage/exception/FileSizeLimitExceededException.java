package com.memorygrove.backend.filestorage.exception;

/**
 * Исключение, выбрасываемое при превышении допустимого размера файла.
 * <p>
 * Возникает, когда размер загружаемого файла превышает максимально
 * допустимый лимит, установленный в конфигурации.
 * </p>
 */
public class FileSizeLimitExceededException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке
     */
    public FileSizeLimitExceededException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause   причина исключения
     */
    public FileSizeLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
