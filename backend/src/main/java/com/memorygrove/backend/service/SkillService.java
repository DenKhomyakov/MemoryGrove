package com.memorygrove.backend.service;

import com.memorygrove.backend.entity.dbo.Skill;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.skill.request.*;
import com.memorygrove.backend.entity.dto.skill.response.CategorySkillStatsDto;
import com.memorygrove.backend.entity.dto.skill.response.SkillResponseDto;
import com.memorygrove.backend.entity.dto.skill.response.TotalSkillStatsDto;
import com.memorygrove.backend.repository.SkillRepository;
import com.memorygrove.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с навыками пользователя
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Создания новых навыков</li>
 *   <li>Просмотра списка навыков</li>
 *   <li>Редактирования навыков</li>
 *   <li>Отметки выполнения навыков</li>
 *   <li>Удаления навыков</li>
 *   <li>Сортировки навыков</li>
 *   <li>Получения статистики по навыкам</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    /**
     * Создание нового навыка
     *
     * @param userId ID пользователя
     * @param createDto DTO с данными для создания
     * @return созданный навык
     * @throws EntityNotFoundException если пользователь не найден
     */
    public SkillResponseDto createSkill(UUID userId, SkillCreateRequestDto createDto) {
        log.info("Создание нового навыка для пользователя: {}", userId);

        User user = findUserById(userId);

        Skill skill = new Skill();
        skill.setUser(user);
        skill.setCategory(createDto.getCategory());
        skill.setTitle(createDto.getTitle().trim());
        skill.setDescription(createDto.getDescription() != null ? createDto.getDescription().trim() : null);
        skill.setCanDoNow(createDto.isCanDoNow());
        skill.setDifficulty(createDto.getDifficulty());
        skill.setImportance(createDto.getImportance());
        skill.setNotes(createDto.getNotes() != null ? createDto.getNotes().trim() : null);

        // Устанавливаем порядок сортировки (в конец списка соответствующей категории)
        Integer maxSortOrder = skillRepository.findByUserIdAndCategoryOrderBySortOrderAscCreatedAtDesc(userId, createDto.getCategory())
                .stream()
                .mapToInt(s -> s.getSortOrder() != null ? s.getSortOrder() : 0)
                .max()
                .orElse(0);
        skill.setSortOrder(maxSortOrder + 1);

        // Если навык отмечен как выполняемый сейчас, устанавливаем дату выполнения
        if (skill.isCanDoNow()) {
            skill.setLastPerformedAt(LocalDateTime.now());
        }

        Skill savedSkill = skillRepository.save(skill);
        log.info("✅ Навык успешно создан с ID: {}", savedSkill.getId());

        return SkillResponseDto.fromEntity(savedSkill);
    }

    /**
     * Получение всех навыков пользователя
     *
     * @param userId ID пользователя
     * @return список навыков
     */
    @Transactional(readOnly = true)
    public List<SkillResponseDto> getUserSkills(UUID userId) {
        log.info("Получение всех навыков пользователя: {}", userId);

        return skillRepository.findByUserIdOrderByCategoryAscSortOrderAscCreatedAtDesc(userId)
                .stream()
                .map(SkillResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получение навыков по категории
     *
     * @param userId ID пользователя
     * @param category категория
     * @return список навыков
     */
    @Transactional(readOnly = true)
    public List<SkillResponseDto> getSkillsByCategory(UUID userId, String category) {
        log.info("Получение навыков пользователя {} по категории {}", userId, category);

        return skillRepository.findByUserIdAndCategoryOrderBySortOrderAscCreatedAtDesc(userId, category)
                .stream()
                .map(SkillResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получение навыка по ID
     *
     * @param skillId ID навыка
     * @param userId ID пользователя
     * @return навык
     * @throws EntityNotFoundException если навык не найден
     * @throws AccessDeniedException если навык принадлежит другому пользователю
     */
    @Transactional(readOnly = true)
    public SkillResponseDto getSkillById(UUID skillId, UUID userId) {
        log.info("Получение навыка {} для пользователя {}", skillId, userId);

        Skill skill = findSkillByIdAndUserId(skillId, userId);
        return SkillResponseDto.fromEntity(skill);
    }

    /**
     * Обновление навыка
     *
     * @param skillId ID навыка
     * @param userId ID пользователя
     * @param updateDto новые данные
     * @return обновлённый навык
     * @throws EntityNotFoundException если навык не найден
     * @throws AccessDeniedException если навык принадлежит другому пользователю
     */
    public SkillResponseDto updateSkill(UUID skillId, UUID userId, SkillUpdateRequestDto updateDto) {
        log.info("Обновление навыка {} для пользователя {}", skillId, userId);

        Skill skill = findSkillByIdAndUserId(skillId, userId);

        skill.setCategory(updateDto.getCategory());
        skill.setTitle(updateDto.getTitle().trim());
        skill.setDescription(updateDto.getDescription() != null ? updateDto.getDescription().trim() : null);
        skill.setDifficulty(updateDto.getDifficulty());
        skill.setImportance(updateDto.getImportance());
        skill.setNotes(updateDto.getNotes() != null ? updateDto.getNotes().trim() : null);

        if (updateDto.getCanDoNow() != null) {
            boolean wasCanDoNow = skill.isCanDoNow();
            skill.setCanDoNow(updateDto.getCanDoNow());

            // Если статус изменился с false на true, обновляем дату выполнения
            if (!wasCanDoNow && updateDto.getCanDoNow()) {
                skill.setLastPerformedAt(LocalDateTime.now());
            }
            // Если статус изменился с true на false, сбрасываем дату выполнения
            else if (wasCanDoNow && !updateDto.getCanDoNow()) {
                skill.setLastPerformedAt(null);
            }
        }

        if (updateDto.getLastPerformedAt() != null) {
            skill.setLastPerformedAt(updateDto.getLastPerformedAt());
        }

        if (updateDto.getSortOrder() != null) {
            skill.setSortOrder(updateDto.getSortOrder());
        }

        Skill updatedSkill = skillRepository.save(skill);
        log.info("✅ Навык {} успешно обновлён", skillId);

        return SkillResponseDto.fromEntity(updatedSkill);
    }

    /**
     * Быстрое переключение статуса навыка (для чек-бокса)
     *
     * @param skillId ID навыка
     * @param userId ID пользователя
     * @param toggleDto данные для переключения
     * @return обновлённый навык
     * @throws EntityNotFoundException если навык не найден
     * @throws AccessDeniedException если навык принадлежит другому пользователю
     */
    public SkillResponseDto toggleSkillStatus(UUID skillId, UUID userId, SkillToggleRequestDto toggleDto) {
        log.info("Переключение статуса навыка {} для пользователя {}", skillId, userId);

        Skill skill = findSkillByIdAndUserId(skillId, userId);

        boolean wasCanDoNow = skill.isCanDoNow();
        skill.setCanDoNow(toggleDto.getCanDoNow());

        // Если навык отмечен как выполненный, обновляем дату
        if (toggleDto.getCanDoNow()) {
            if (toggleDto.getLastPerformedAt() != null) {
                skill.setLastPerformedAt(toggleDto.getLastPerformedAt());
            } else {
                skill.setLastPerformedAt(LocalDateTime.now());
            }
        } else {
            // Если сняли отметку, очищаем дату выполнения
            skill.setLastPerformedAt(null);
        }

        Skill updatedSkill = skillRepository.save(skill);
        log.info("✅ Статус навыка {} переключён: canDoNow={}", skillId, toggleDto.getCanDoNow());

        return SkillResponseDto.fromEntity(updatedSkill);
    }

    /**
     * Удаление навыка
     *
     * @param skillId ID навыка
     * @param userId ID пользователя
     * @throws EntityNotFoundException если навык не найден или не принадлежит пользователю
     */
    public void deleteSkill(UUID skillId, UUID userId) {
        log.info("Удаление навыка {} для пользователя {}", skillId, userId);

        if (!skillRepository.existsByIdAndUserId(skillId, userId)) {
            throw new EntityNotFoundException(
                    String.format("Навык с ID '%s' не найден или не принадлежит пользователю", skillId)
            );
        }

        skillRepository.deleteById(skillId);
        log.info("✅ Навык {} успешно удалён", skillId);
    }

    /**
     * Изменение порядка сортировки навыков
     *
     * @param userId ID пользователя
     * @param skillIds список ID навыков в нужном порядке
     * @throws EntityNotFoundException если навык не найден
     * @throws AccessDeniedException если навык принадлежит другому пользователю
     */
    public void reorderSkills(UUID userId, List<UUID> skillIds) {
        log.info("Обновление порядка сортировки навыков для пользователя {}", userId);

        List<Skill> skills = skillRepository.findByUserIdOrderByCategoryAscSortOrderAscCreatedAtDesc(userId);

        for (int index = 0; index < skillIds.size(); index++) {
            UUID currentSkillId = skillIds.get(index);
            final int newOrder = index + 1;

            skills.stream()
                    .filter(skill -> skill.getId().equals(currentSkillId))
                    .findFirst()
                    .ifPresent(skill -> skill.setSortOrder(newOrder));
        }

        skillRepository.saveAll(skills);
        log.info("✅ Порядок сортировки навыков обновлён");
    }

    /**
     * Получение статистики по категориям
     *
     * @param userId ID пользователя
     * @return статистика по каждой категории
     */
    @Transactional(readOnly = true)
    public List<CategorySkillStatsDto> getCategoryStats(UUID userId) {
        log.info("Получение статистики по категориям для пользователя {}", userId);

        List<Object[]> stats = skillRepository.getCategoryStats(userId);
        List<CategorySkillStatsDto> result = new ArrayList<>();

        for (Object[] stat : stats) {
            String category = (String) stat[0];
            long total = ((Number) stat[1]).longValue();
            long mastered = stat[2] != null ? ((Number) stat[2]).longValue() : 0;
            int percentage = total > 0 ? (int) ((mastered * 100) / total) : 0;

            CategorySkillStatsDto dto = new CategorySkillStatsDto(
                    category,
                    SkillResponseDto.getCategoryDisplayName(category),
                    total,
                    mastered,
                    percentage
            );
            result.add(dto);
        }

        return result;
    }

    /**
     * Получение общей статистики по всем навыкам
     *
     * @param userId ID пользователя
     * @return общая статистика
     */
    @Transactional(readOnly = true)
    public TotalSkillStatsDto getTotalStats(UUID userId) {
        log.info("Получение общей статистики для пользователя {}", userId);

        List<Object[]> stats = skillRepository.getTotalStats(userId);

        if (stats == null || stats.isEmpty() || stats.get(0)[0] == null) {
            return new TotalSkillStatsDto(0, 0, 0);
        }

        Object[] stat = stats.get(0);
        long total = ((Number) stat[0]).longValue();
        long mastered = stat[1] != null ? ((Number) stat[1]).longValue() : 0;
        int percentage = total > 0 ? (int) ((mastered * 100) / total) : 0;

        return new TotalSkillStatsDto(total, mastered, percentage);
    }

    /**
     * Поиск пользователя по ID
     *
     * @param userId ID пользователя
     * @return сущность пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */
    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с ID '%s' не найден", userId)
                ));
    }

    /**
     * Поиск навыка по ID с проверкой принадлежности пользователю
     *
     * @param skillId ID навыка
     * @param userId ID пользователя
     * @return сущность навыка
     * @throws EntityNotFoundException если навык не найден
     * @throws AccessDeniedException если навык принадлежит другому пользователю
     */
    private Skill findSkillByIdAndUserId(UUID skillId, UUID userId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Навык с ID '%s' не найден", skillId)
                ));

        if (!skill.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("У вас нет прав на доступ к этому навыку");
        }

        return skill;
    }
}
