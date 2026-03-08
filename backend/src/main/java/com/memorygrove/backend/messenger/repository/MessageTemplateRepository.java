package com.memorygrove.backend.messenger.repository;

import com.memorygrove.backend.messenger.entity.dbo.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с шаблонами сообщений
 */
@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, UUID> {

    /**
     * Найти все шаблоны пользователя (включая системные)
     *
     * @param userId ID пользователя
     * @return список шаблонов
     */
    @Query("SELECT mt FROM MessageTemplate mt " +
            "WHERE mt.user.id = :userId OR mt.isDefault = true " +
            "ORDER BY mt.sortOrder ASC, mt.title ASC")
    List<MessageTemplate> findUserTemplates(@Param("userId") UUID userId);

    /**
     * Найти только пользовательские шаблоны
     *
     * @param userId ID пользователя
     * @return список пользовательских шаблонов
     */
    List<MessageTemplate> findByUserIdOrderBySortOrderAscTitleAsc(UUID userId);

    /**
     * Найти системные шаблоны
     *
     * @return список системных шаблонов
     */
    List<MessageTemplate> findByIsDefaultTrueOrderBySortOrderAscTitleAsc();

    /**
     * Проверить, является ли шаблон системным
     *
     * @param templateId ID шаблона
     * @return true если системный
     */
    @Query("SELECT COUNT(mt) > 0 FROM MessageTemplate mt " +
            "WHERE mt.id = :templateId AND mt.isDefault = true")
    boolean isSystemTemplate(@Param("templateId") UUID templateId);

    /**
     * Удалить все шаблоны пользователя
     *
     * @param userId ID пользователя
     */
    void deleteByUserId(UUID userId);
}
