package com.memorygrove.backend.filestorage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Клас для конфигурации параметров файлового хранилища.
 * <p>
 * Содержит настройки для загрузки и хранения фотографий:
 * пути к директориям, ограничения по размеру, допустимые типы файлов.
 * Значения загружаются из application-dev.properties с префиксом "file.storage".
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "file.storage")
@Data
@Validated
public class FileStorageProperties {

    /**
     * Директория для загрузки оригинальных файлов.
     * Относительный путь (./uploads) для разработки.
     */
    @NotBlank(message = "Путь для загрузки файлов не может быть пустым")
    private String uploadDir;

    /**
     * Директория для хранения миниатюр изображений.
     * Относительный путь (./thumbnails) для разработки.
     */
    @NotBlank(message = "Путь для миниатюр не может быть пустым")
    private String thumbnailDir;

    /**
     * Максимальный размер загружаемого файла в байтах.
     * По умолчанию 10 MB (10485760 байт).
     */
    @NotNull(message = "Максимальный размер файла не может быть пустым")
    @Min(value = 1, message = "Максимальный размер должен быть больше 0")
    @Max(value = 104857600, message = "Максимальный размер не может превышать 100 MB")
    private Long maxFileSize = 10485760L;

    /**
     * Список разрешенных MIME-типов для загрузки.
     * По умолчанию: JPEG, PNG, GIF, WebP.
     */
    private List<@NotBlank String> allowedTypes = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    /**
     * Размер миниатюры в пикселях (ширина и высота).
     * По умолчанию 300x300 пикселей.
     */
    @Min(value = 50, message = "Размер миниатюры должен быть не менее 50 пикселей")
    @Max(value = 1000, message = "Размер миниатюры не может превышать 1000 пикселей")
    private int thumbnailSize = 300;
}
