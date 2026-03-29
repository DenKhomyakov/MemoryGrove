package com.memorygrove.backend.repository;

import com.memorygrove.backend.entity.dbo.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с навыками пользователя
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    /**
     * Найти все навыки пользователя, отсортированные по категории и порядку
     *
     * @param userId ID пользователя
     * @return список навыков
     */
    List<Skill> findByUserIdOrderByCategoryAscSortOrderAscCreatedAtDesc(UUID userId);

    /**
     * Найти навыки пользователя по категории
     *
     * @param userId ID пользователя
     * @param category категория
     * @return список навыков
     */
    List<Skill> findByUserIdAndCategoryOrderBySortOrderAscCreatedAtDesc(UUID userId, String category);

    /**
     * Найти навыки, которые пользователь может выполнять сейчас
     *
     * @param userId ID пользователя
     * @return список освоенных навыков
     */
    List<Skill> findByUserIdAndCanDoNowTrueOrderByCategoryAscSortOrderAsc(UUID userId);

    /**
     * Найти навыки, которые пользователь пока не может выполнять
     *
     * @param userId ID пользователя
     * @return список неосвоенных навыков
     */
    List<Skill> findByUserIdAndCanDoNowFalseOrderByCategoryAscSortOrderAsc(UUID userId);

    /**
     * Проверить, принадлежит ли навык пользователю
     */
    @Query("SELECT COUNT(s) > 0 FROM Skill s WHERE s.id = :skillId AND s.user.id = :userId")
    boolean existsByIdAndUserId(@Param("skillId") UUID skillId, @Param("userId") UUID userId);

    /**
     * Удалить все навыки пользователя
     */
    void deleteByUserId(UUID userId);

    /**
     * Получить статистику по категориям для пользователя
     * <p>
     * Возвращает для каждой категории:
     * - название категории
     * - общее количество навыков
     * - количество освоенных навыков
     * </p>
     *
     * @param userId ID пользователя
     * @return список объектов [категория, общее количество, количество освоенных]
     */
    @Query("SELECT s.category, COUNT(s), SUM(CASE WHEN s.canDoNow = true THEN 1 ELSE 0 END) " +
            "FROM Skill s WHERE s.user.id = :userId GROUP BY s.category")
    List<Object[]> getCategoryStats(@Param("userId") UUID userId);

    /**
     * Получить общую статистику по пользователю
     *
     * @param userId ID пользователя
     * @return список объектов [общее количество, количество освоенных]
     */
    @Query("SELECT COUNT(s), SUM(CASE WHEN s.canDoNow = true THEN 1 ELSE 0 END) " +
            "FROM Skill s WHERE s.user.id = :userId")
    List<Object[]> getTotalStats(@Param("userId") UUID userId);
}
