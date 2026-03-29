package com.memorygrove.backend.service;

import com.memorygrove.backend.entity.dbo.SignificantPlace;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.place.request.PlaceCreateRequestDto;
import com.memorygrove.backend.entity.dto.place.request.PlaceUpdateRequestDto;
import com.memorygrove.backend.entity.dto.place.response.PlaceResponseDto;
import com.memorygrove.backend.repository.SignificantPlaceRepository;
import com.memorygrove.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы со значимыми местами пользователей
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Создания новых мест</li>
 *   <li>Просмотра списка мест</li>
 *   <li>Редактирования мест</li>
 *   <li>Удаления мест</li>
 *   <li>Сортировки мест</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class SignificantPlaceService {

    private final SignificantPlaceRepository placeRepository;
    private final UserRepository userRepository;

    /**
     * Создание нового значимого места
     *
     * @param userId ID пользователя
     * @param createDto DTO с данными для создания
     * @return созданное место
     * @throws EntityNotFoundException если пользователь не найден
     */
    public PlaceResponseDto createPlace(UUID userId, PlaceCreateRequestDto createDto) {
        log.info("Создание нового значимого места для пользователя: {}", userId);

        User user = findUserById(userId);

        SignificantPlace place = new SignificantPlace();
        place.setUser(user);
        place.setTitle(createDto.getTitle().trim());
        place.setAddress(createDto.getAddress().trim());
        place.setDescription(createDto.getDescription() != null ? createDto.getDescription().trim() : null);
        place.setCategory(createDto.getCategory());
        place.setHome(createDto.isHome());
        place.setWork(createDto.isWork());
        place.setMedical(createDto.isMedical());

        // Устанавливаем порядок сортировки (в конец списка)
        Integer maxSortOrder = placeRepository.findByUserIdOrderBySortOrderAscCreatedAtDesc(userId)
                .stream()
                .mapToInt(p -> p.getSortOrder() != null ? p.getSortOrder() : 0)
                .max()
                .orElse(0);
        place.setSortOrder(maxSortOrder + 1);

        SignificantPlace savedPlace = placeRepository.save(place);
        log.info("✅ Место успешно создано с ID: {}", savedPlace.getId());

        return PlaceResponseDto.fromEntity(savedPlace);
    }

    /**
     * Получение всех мест пользователя
     *
     * @param userId ID пользователя
     * @return список мест
     */
    @Transactional(readOnly = true)
    public List<PlaceResponseDto> getUserPlaces(UUID userId) {
        log.info("Получение всех мест пользователя: {}", userId);

        return placeRepository.findByUserIdOrderBySortOrderAscCreatedAtDesc(userId)
                .stream()
                .map(PlaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получение места по ID
     *
     * @param placeId ID места
     * @param userId ID пользователя
     * @return место
     * @throws EntityNotFoundException если место не найдено
     * @throws AccessDeniedException если место принадлежит другому пользователю
     */
    @Transactional(readOnly = true)
    public PlaceResponseDto getPlaceById(UUID placeId, UUID userId) {
        log.info("Получение места {} для пользователя {}", placeId, userId);

        SignificantPlace place = findPlaceByIdAndUserId(placeId, userId);
        return PlaceResponseDto.fromEntity(place);
    }

    /**
     * Обновление места
     *
     * @param placeId ID места
     * @param userId ID пользователя
     * @param updateDto новые данные
     * @return обновлённое место
     * @throws EntityNotFoundException если место не найдено
     * @throws AccessDeniedException если место принадлежит другому пользователю
     */
    public PlaceResponseDto updatePlace(UUID placeId, UUID userId, PlaceUpdateRequestDto updateDto) {
        log.info("Обновление места {} для пользователя {}", placeId, userId);

        SignificantPlace place = findPlaceByIdAndUserId(placeId, userId);

        place.setTitle(updateDto.getTitle().trim());
        place.setAddress(updateDto.getAddress().trim());
        place.setDescription(updateDto.getDescription() != null ? updateDto.getDescription().trim() : null);
        place.setCategory(updateDto.getCategory());
        place.setHome(updateDto.isHome());
        place.setWork(updateDto.isWork());
        place.setMedical(updateDto.isMedical());

        if (updateDto.getSortOrder() != null) {
            place.setSortOrder(updateDto.getSortOrder());
        }

        SignificantPlace updatedPlace = placeRepository.save(place);
        log.info("✅ Место {} успешно обновлено", placeId);

        return PlaceResponseDto.fromEntity(updatedPlace);
    }

    /**
     * Удаление места
     *
     * @param placeId ID места
     * @param userId ID пользователя
     * @throws EntityNotFoundException если место не найдено или не принадлежит пользователю
     */
    public void deletePlace(UUID placeId, UUID userId) {
        log.info("Удаление места {} для пользователя {}", placeId, userId);

        if (!placeRepository.existsByIdAndUserId(placeId, userId)) {
            throw new EntityNotFoundException(
                    String.format("Место с ID '%s' не найдено или не принадлежит пользователю", placeId)
            );
        }

        placeRepository.deleteById(placeId);
        log.info("✅ Место {} успешно удалено", placeId);
    }

    /**
     * Обновление порядка сортировки мест
     *
     * @param userId ID пользователя
     * @param placeIds список ID мест в нужном порядке
     * @throws EntityNotFoundException если место не найдено
     * @throws AccessDeniedException если место принадлежит другому пользователю
     */
    public void reorderPlaces(UUID userId, List<UUID> placeIds) {
        log.info("Обновление порядка сортировки мест для пользователя {}", userId);

        List<SignificantPlace> places = placeRepository.findByUserIdOrderBySortOrderAscCreatedAtDesc(userId);

        for (int i = 0; i < placeIds.size(); i++) {
            UUID placeId = placeIds.get(i);
            final int newOrder = i + 1;

            places.stream()
                    .filter(p -> p.getId().equals(placeId))
                    .findFirst()
                    .ifPresent(p -> p.setSortOrder(newOrder));
        }

        placeRepository.saveAll(places);
        log.info("✅ Порядок сортировки обновлён");
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
     * Поиск места по ID с проверкой принадлежности пользователю
     *
     * @param placeId ID места
     * @param userId ID пользователя
     * @return сущность места
     * @throws EntityNotFoundException если место не найдено
     * @throws AccessDeniedException если место принадлежит другому пользователю
     */
    private SignificantPlace findPlaceByIdAndUserId(UUID placeId, UUID userId) {
        SignificantPlace place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Место с ID '%s' не найдено", placeId)
                ));

        if (!place.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("У вас нет прав на доступ к этому месту");
        }

        return place;
    }
}
