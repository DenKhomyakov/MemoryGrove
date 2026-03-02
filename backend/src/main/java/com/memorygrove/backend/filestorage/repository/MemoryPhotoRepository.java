package com.memorygrove.backend.filestorage.repository;

import com.memorygrove.backend.filestorage.entity.dbo.MemoryPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с фотографиями пользователей.
 * <p>
 * Предоставляет методы для поиска, фильтрации и проверки фотографий,
 * а также для получения статистики по загрузкам.
 * </p>
 */
@Repository
public interface MemoryPhotoRepository extends JpaRepository<MemoryPhoto, UUID> {

    /**
     * Находит все фотографии пользователя, отсортированные по дате загрузки.
     *
     * @param userId идентификатор пользователя
     * @return список фотографий пользователя (от новых к старым)
     */
    List<MemoryPhoto> findByUserIdOrderByUploadDateDesc(UUID userId);

    /**
     * Находит фотографию по ID с проверкой принадлежности пользователю.
     *
     * @param id     идентификатор фотографии
     * @param userId идентификатор пользователя
     * @return Optional с фотографией, если она существует и принадлежит пользователю
     */
    @Query("SELECT mp FROM MemoryPhoto mp WHERE mp.id = :id AND mp.userId = :userId")
    Optional<MemoryPhoto> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    /**
     * Проверяет, существует ли фотография с указанным ID и принадлежит ли она пользователю.
     *
     * @param id     идентификатор фотографии
     * @param userId идентификатор пользователя
     * @return true если фотография существует и принадлежит пользователю
     */
    @Query("SELECT COUNT(mp) > 0 FROM MemoryPhoto mp WHERE mp.id = :id AND mp.userId = :userId")
    boolean existsByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    /**
     * Удаляет все фотографии пользователя.
     *
     * @param userId идентификатор пользователя
     */
    void deleteByUserId(UUID userId);

    /**
     * Находит фотографии пользователя за определённый год.
     *
     * @param userId идентификатор пользователя
     * @param year   год съёмки
     * @return список фотографий за указанный год
     */
    List<MemoryPhoto> findByUserIdAndYearOrderByUploadDateDesc(UUID userId, Integer year);

    /**
     * Находит фотографии пользователя по месту съёмки.
     *
     * @param userId   идентификатор пользователя
     * @param location место съёмки
     * @return список фотографий из указанного места
     */
    List<MemoryPhoto> findByUserIdAndLocationContainingIgnoreCaseOrderByUploadDateDesc(UUID userId, String location);

    /**
     * Находит фотографии пользователя, на которых есть указанный человек.
     *
     * @param userId идентификатор пользователя
     * @param person имя человека
     * @return список фотографий с указанным человеком
     */
    @Query("SELECT mp FROM MemoryPhoto mp WHERE mp.userId = :userId AND LOWER(mp.people) LIKE LOWER(CONCAT('%', :person, '%'))")
    List<MemoryPhoto> findByUserIdAndPerson(@Param("userId") UUID userId, @Param("person") String person);

    /**
     * Подсчитывает количество фотографий пользователя.
     *
     * @param userId идентификатор пользователя
     * @return количество фотографий
     */
    long countByUserId(UUID userId);

    /**
     * Получает список всех годов, за которые есть фотографии у пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список годов (без повторений)
     */
    @Query("SELECT DISTINCT mp.year FROM MemoryPhoto mp WHERE mp.userId = :userId AND mp.year IS NOT NULL ORDER BY mp.year DESC")
    List<Integer> findDistinctYearsByUserId(@Param("userId") UUID userId);

    /**
     * Получает статистику по загрузкам пользователя.
     *
     * @param userId идентификатор пользователя
     * @return массив статистики [общее количество, общий размер, количество по годам]
     */
    @Query("SELECT COUNT(mp), COALESCE(SUM(mp.fileSize), 0) FROM MemoryPhoto mp WHERE mp.userId = :userId")
    List<Object[]> getUserPhotoStats(@Param("userId") UUID userId);
}
