package com.memorygrove.backend.repository;

import com.memorygrove.backend.entity.dbo.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с интересами пользователей
 */
@Repository
public interface InterestRepository extends JpaRepository<Interest, UUID> {

    /**
     * Найти все интересы пользователя, отсортированные по категории и порядку
     *
     * @param userId ID пользователя
     * @return список интересов
     */
    List<Interest> findByUserIdOrderByCategoryAscSortOrderAscCreatedAtDesc(UUID userId);

    /**
     * Найти интересы пользователя по категории
     *
     * @param userId ID пользователя
     * @param category категория
     * @return список интересов
     */
    List<Interest> findByUserIdAndCategoryOrderBySortOrderAscCreatedAtDesc(UUID userId, String category);

    /**
     * Найти текущие интересы (которые нравятся сейчас)
     *
     * @param userId ID пользователя
     * @return список текущих интересов
     */
    List<Interest> findByUserIdAndIsCurrentTrueOrderByCategoryAscSortOrderAsc(UUID userId);

    /**
     * Найти интересы с положительной оценкой
     *
     * @param userId ID пользователя
     * @return список интересов с liked = true
     */
    List<Interest> findByUserIdAndLikedTrueOrderByCategoryAsc(UUID userId);

    /**
     * Проверить, принадлежит ли интерес пользователю
     */
    @Query("SELECT COUNT(i) > 0 FROM Interest i WHERE i.id = :interestId AND i.user.id = :userId")
    boolean existsByIdAndUserId(@Param("interestId") UUID interestId, @Param("userId") UUID userId);

    /**
     * Удалить все интересы пользователя
     */
    void deleteByUserId(UUID userId);

    /**
     * Получить статистику по категориям для пользователя
     * <p>
     * Возвращает для каждой категории:
     * - название категории
     * - общее количество элементов
     * - количество элементов с liked = true
     * </p>
     *
     * @param userId ID пользователя
     * @return список объектов [категория, количество, количество лайков]
     */
    @Query("SELECT i.category, COUNT(i), SUM(CASE WHEN i.liked = true THEN 1 ELSE 0 END) " +
            "FROM Interest i WHERE i.user.id = :userId GROUP BY i.category")
    List<Object[]> getCategoryStats(@Param("userId") UUID userId);
}
