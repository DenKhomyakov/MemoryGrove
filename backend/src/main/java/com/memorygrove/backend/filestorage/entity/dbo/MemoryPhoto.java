package com.memorygrove.backend.filestorage.entity.dbo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность "Фотография" для модуля цифровой памяти.
 * <p>
 * Представляет собой фотографию, загруженную пользователем в свой личный архив.
 * Содержит метаданные: описание, место съёмки, год, людей на фото,
 * а также пути к оригинальному файлу и миниатюре.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "memory_photos")
public class MemoryPhoto {

    /**
     * Уникальный идентификатор фотографии.
     * Генерируется автоматически при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Идентификатор пользователя, загрузившего фотографию.
     * Связь с таблицей users.
     */
    @NotNull(message = "ID пользователя обязателен")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Оригинальное имя файла (как было на устройстве пользователя).
     */
    @NotBlank(message = "Имя файла не может быть пустым")
    @Size(max = 255, message = "Имя файла не должно превышать 255 символов")
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /**
     * Путь к сохранённому файлу на сервере.
     * Относительный путь от корня приложения.
     */
    @NotBlank(message = "Путь к файлу не может быть пустым")
    @Size(max = 500, message = "Путь к файлу не должен превышать 500 символов")
    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    /**
     * Путь к миниатюре изображения.
     * Может быть null, если миниатюра не создавалась.
     */
    @Size(max = 500, message = "Путь к миниатюре не должен превышать 500 символов")
    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    /**
     * Описание фотографии.
     * Произвольный текст от пользователя.
     */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Column(name = "description")
    private String description;

    /**
     * Место съёмки.
     * Например: "Москва, ВДНХ" или "Домашний архив".
     */
    @Size(max = 255, message = "Название места не должно превышать 255 символов")
    @Column(name = "location")
    private String location;

    /**
     * Год, к которому относится фотография.
     */
    @Column(name = "year")
    private Integer year;

    /**
     * Люди на фотографии.
     * Можно хранить как список имён через запятую или JSON.
     */
    @Size(max = 500, message = "Список людей не должен превышать 500 символов")
    @Column(name = "people")
    private String people;

    /**
     * Дата и время загрузки фотографии.
     */
    @NotNull(message = "Дата загрузки обязательна")
    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    /**
     * Размер файла в байтах.
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * MIME-тип файла (например, "image/jpeg").
     */
    @Size(max = 100, message = "MIME-тип не должен превышать 100 символов")
    @Column(name = "content_type")
    private String contentType;

    /**
     * Дата и время создания записи.
     * Устанавливается автоматически.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего редактирования метаданных.
     * Обновляется автоматически.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Проверяет, была ли фотография отредактирована.
     *
     * @return true если запись редактировалась после создания
     */
    @Transient
    public boolean isEdited() {
        return updatedAt != null && !updatedAt.equals(createdAt);
    }

    /**
     * Получает URL для скачивания оригинального файла.
     *
     * @return URL для доступа к файлу
     */
    @Transient
    public String getDownloadUrl() {
        return "/api/memory/photos/" + id + "/download";
    }

    /**
     * Получает URL для доступа к миниатюре.
     *
     * @return URL для доступа к миниатюре или null, если миниатюры нет
     */
    @Transient
    public String getThumbnailUrl() {
        return thumbnailPath != null ? "/api/memory/photos/" + id + "/thumbnail" : null;
    }
}
