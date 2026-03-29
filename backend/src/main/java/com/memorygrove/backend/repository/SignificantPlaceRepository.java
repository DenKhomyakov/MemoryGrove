package com.memorygrove.backend.repository;

import com.memorygrove.backend.entity.dbo.SignificantPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы со значимыми местами пользователей
 */
@Repository
public interface SignificantPlaceRepository extends JpaRepository<SignificantPlace, UUID> {

    /**
     * Найти все места пользователя, отсортированные по порядку
     *
     * @param userId ID пользователя
     * @return список мест
     */
    List<SignificantPlace> findByUserIdOrderBySortOrderAscCreatedAtDesc(UUID userId);

    /**
     * Найти места пользователя по категории
     *
     * @param userId ID пользователя
     * @param category категория
     * @return список мест
     */
    List<SignificantPlace> findByUserIdAndCategoryOrderBySortOrderAsc(UUID userId, String category);

    /**
     * Найти домашний адрес пользователя
     *
     * @param userId ID пользователя
     * @return список домашних адресов (обычно один)
     */
    List<SignificantPlace> findByUserIdAndIsHomeTrue(UUID userId);

    /**
     * Найти медицинские учреждения пользователя
     *
     * @param userId ID пользователя
     * @return список медицинских учреждений
     */
    List<SignificantPlace> findByUserIdAndIsMedicalTrue(UUID userId);

    /**
     * Проверить, принадлежит ли место пользователю
     */
    @Query("SELECT COUNT(sp) > 0 FROM SignificantPlace sp WHERE sp.id = :placeId AND sp.user.id = :userId")
    boolean existsByIdAndUserId(@Param("placeId") UUID placeId, @Param("userId") UUID userId);

    /**
     * Удалить все места пользователя
     */
    void deleteByUserId(UUID userId);
}
