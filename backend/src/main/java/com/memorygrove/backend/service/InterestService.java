package com.memorygrove.backend.service;

import com.memorygrove.backend.entity.dbo.Interest;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.interest.request.*;
import com.memorygrove.backend.entity.dto.interest.response.CategoryStatsDto;
import com.memorygrove.backend.entity.dto.interest.response.InterestResponseDto;
import com.memorygrove.backend.repository.InterestRepository;
import com.memorygrove.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с интересами пользователей
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Создания новых интересов</li>
 *   <li>Просмотра списка интересов</li>
 *   <li>Редактирования интересов</li>
 *   <li>Оценки (лайк/дизлайк) интересов</li>
 *   <li>Удаления интересов</li>
 *   <li>Сортировки интересов</li>
 *   <li>Получения статистики по категориям</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class InterestService {

    private final InterestRepository interestRepository;
    private final UserRepository userRepository;

    /**
     * Создание нового интереса
     *
     * @param userId ID пользователя
     * @param createDto DTO с данными для создания
     * @return созданный интерес
     * @throws EntityNotFoundException если пользователь не найден
     */
    public InterestResponseDto createInterest(UUID userId, InterestCreateRequestDto createDto) {
        log.info("Создание нового интереса для пользователя: {}", userId);

        User user = findUserById(userId);

        Interest interest = new Interest();
        interest.setUser(user);
        interest.setCategory(createDto.getCategory());
        interest.setTitle(createDto.getTitle().trim());
        interest.setCreator(createDto.getCreator() != null ? createDto.getCreator().trim() : null);
        interest.setYear(createDto.getYear());
        interest.setGenre(createDto.getGenre() != null ? createDto.getGenre().trim() : null);
        interest.setDescription(createDto.getDescription() != null ? createDto.getDescription().trim() : null);
        interest.setLiked(createDto.getLiked());
        interest.setRating(createDto.getRating());
        interest.setCurrent(createDto.isCurrent());

        // Устанавливаем порядок сортировки (в конец списка соответствующей категории)
        Integer maxSortOrder = interestRepository.findByUserIdAndCategoryOrderBySortOrderAscCreatedAtDesc(userId, createDto.getCategory())
                .stream()
                .mapToInt(i -> i.getSortOrder() != null ? i.getSortOrder() : 0)
                .max()
                .orElse(0);
        interest.setSortOrder(maxSortOrder + 1);

        Interest savedInterest = interestRepository.save(interest);
        log.info("✅ Интерес успешно создан с ID: {}", savedInterest.getId());

        return InterestResponseDto.fromEntity(savedInterest);
    }

    /**
     * Получение всех интересов пользователя
     *
     * @param userId ID пользователя
     * @return список интересов
     */
    @Transactional(readOnly = true)
    public List<InterestResponseDto> getUserInterests(UUID userId) {
        log.info("Получение всех интересов пользователя: {}", userId);

        return interestRepository.findByUserIdOrderByCategoryAscSortOrderAscCreatedAtDesc(userId)
                .stream()
                .map(InterestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получение интересов по категории
     *
     * @param userId ID пользователя
     * @param category категория
     * @return список интересов
     */
    @Transactional(readOnly = true)
    public List<InterestResponseDto> getInterestsByCategory(UUID userId, String category) {
        log.info("Получение интересов пользователя {} по категории {}", userId, category);

        return interestRepository.findByUserIdAndCategoryOrderBySortOrderAscCreatedAtDesc(userId, category)
                .stream()
                .map(InterestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получение интереса по ID
     *
     * @param interestId ID интереса
     * @param userId ID пользователя
     * @return интерес
     * @throws EntityNotFoundException если интерес не найден
     * @throws AccessDeniedException если интерес принадлежит другому пользователю
     */
    @Transactional(readOnly = true)
    public InterestResponseDto getInterestById(UUID interestId, UUID userId) {
        log.info("Получение интереса {} для пользователя {}", interestId, userId);

        Interest interest = findInterestByIdAndUserId(interestId, userId);
        return InterestResponseDto.fromEntity(interest);
    }

    /**
     * Обновление интереса
     *
     * @param interestId ID интереса
     * @param userId ID пользователя
     * @param updateDto новые данные
     * @return обновлённый интерес
     * @throws EntityNotFoundException если интерес не найден
     * @throws AccessDeniedException если интерес принадлежит другому пользователю
     */
    public InterestResponseDto updateInterest(UUID interestId, UUID userId, InterestUpdateRequestDto updateDto) {
        log.info("Обновление интереса {} для пользователя {}", interestId, userId);

        Interest interest = findInterestByIdAndUserId(interestId, userId);

        interest.setCategory(updateDto.getCategory());
        interest.setTitle(updateDto.getTitle().trim());
        interest.setCreator(updateDto.getCreator() != null ? updateDto.getCreator().trim() : null);
        interest.setYear(updateDto.getYear());
        interest.setGenre(updateDto.getGenre() != null ? updateDto.getGenre().trim() : null);
        interest.setDescription(updateDto.getDescription() != null ? updateDto.getDescription().trim() : null);
        interest.setLiked(updateDto.getLiked());
        interest.setRating(updateDto.getRating());

        if (updateDto.getIsCurrent() != null) {
            interest.setCurrent(updateDto.getIsCurrent());
        }

        if (updateDto.getSortOrder() != null) {
            interest.setSortOrder(updateDto.getSortOrder());
        }

        Interest updatedInterest = interestRepository.save(interest);
        log.info("✅ Интерес {} успешно обновлён", interestId);

        return InterestResponseDto.fromEntity(updatedInterest);
    }

    /**
     * Обновление оценки (лайк/дизлайк) интереса
     *
     * @param interestId ID интереса
     * @param userId ID пользователя
     * @param likeDto данные оценки
     * @return обновлённый интерес
     * @throws EntityNotFoundException если интерес не найден
     * @throws AccessDeniedException если интерес принадлежит другому пользователю
     */
    public InterestResponseDto updateLike(UUID interestId, UUID userId, InterestLikeRequestDto likeDto) {
        log.info("Обновление оценки интереса {} для пользователя {}", interestId, userId);

        Interest interest = findInterestByIdAndUserId(interestId, userId);

        interest.setLiked(likeDto.getLiked());

        if (likeDto.getRating() != null) {
            interest.setRating(likeDto.getRating());
        }

        Interest updatedInterest = interestRepository.save(interest);
        log.info("✅ Оценка интереса {} обновлена: liked={}", interestId, likeDto.getLiked());

        return InterestResponseDto.fromEntity(updatedInterest);
    }

    /**
     * Удаление интереса
     *
     * @param interestId ID интереса
     * @param userId ID пользователя
     * @throws EntityNotFoundException если интерес не найден или не принадлежит пользователю
     */
    public void deleteInterest(UUID interestId, UUID userId) {
        log.info("Удаление интереса {} для пользователя {}", interestId, userId);

        if (!interestRepository.existsByIdAndUserId(interestId, userId)) {
            throw new EntityNotFoundException(
                    String.format("Интерес с ID '%s' не найдено или не принадлежит пользователю", interestId)
            );
        }

        interestRepository.deleteById(interestId);
        log.info("✅ Интерес {} успешно удалён", interestId);
    }

    /**
     * Изменение порядка сортировки интересов
     *
     * @param userId ID пользователя
     * @param interestIds список ID интересов в нужном порядке
     * @throws EntityNotFoundException если интерес не найден
     * @throws AccessDeniedException если интерес принадлежит другому пользователю
     */
    public void reorderInterests(UUID userId, List<UUID> interestIds) {
        log.info("Обновление порядка сортировки интересов для пользователя {}", userId);

        List<Interest> interests = interestRepository.findByUserIdOrderByCategoryAscSortOrderAscCreatedAtDesc(userId);

        for (int index = 0; index < interestIds.size(); index++) {
            UUID currentInterestId = interestIds.get(index);
            final int newOrder = index + 1;

            interests.stream()
                    .filter(interest -> interest.getId().equals(currentInterestId))
                    .findFirst()
                    .ifPresent(interest -> interest.setSortOrder(newOrder));
        }

        interestRepository.saveAll(interests);
        log.info("✅ Порядок сортировки интересов обновлён");
    }

    /**
     * Получение статистики по категориям
     *
     * @param userId ID пользователя
     * @return статистика по категориям
     */
    @Transactional(readOnly = true)
    public List<CategoryStatsDto> getCategoryStats(UUID userId) {
        log.info("Получение статистики по категориям для пользователя {}", userId);

        List<Object[]> stats = interestRepository.getCategoryStats(userId);
        List<CategoryStatsDto> result = new ArrayList<>();

        for (Object[] stat : stats) {
            String category = (String) stat[0];
            long count = ((Number) stat[1]).longValue();
            long likesCount = stat[2] != null ? ((Number) stat[2]).longValue() : 0;

            CategoryStatsDto dto = new CategoryStatsDto(
                    category,
                    CategoryStatsDto.getDisplayName(category),
                    count,
                    likesCount
            );
            result.add(dto);
        }

        return result;
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
     * Поиск интереса по ID с проверкой принадлежности пользователю
     *
     * @param interestId ID интереса
     * @param userId ID пользователя
     * @return сущность интереса
     * @throws EntityNotFoundException если интерес не найден
     * @throws AccessDeniedException если интерес принадлежит другому пользователю
     */
    private Interest findInterestByIdAndUserId(UUID interestId, UUID userId) {
        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Интерес с ID '%s' не найден", interestId)
                ));

        if (!interest.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("У вас нет прав на доступ к этому интересу");
        }

        return interest;
    }
}
