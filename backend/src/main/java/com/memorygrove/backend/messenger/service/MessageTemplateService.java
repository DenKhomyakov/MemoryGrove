package com.memorygrove.backend.messenger.service;

import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.messenger.entity.dbo.MessageTemplate;
import com.memorygrove.backend.messenger.entity.dto.request.MessageTemplateRequestDto;
import com.memorygrove.backend.messenger.entity.dto.response.MessageTemplateResponseDto;
import com.memorygrove.backend.messenger.repository.MessageTemplateRepository;
import com.memorygrove.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с шаблонами сообщений
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Создания системных шаблонов при запуске</li>
 *   <li>Создания пользовательских шаблонов</li>
 *   <li>Просмотра доступных шаблонов</li>
 *   <li>Обновления и удаления шаблонов</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MessageTemplateService {

    private final MessageTemplateRepository templateRepository;
    private final UserRepository userRepository;

    /**
     * Инициализация системных шаблонов при запуске приложения
     */
    @PostConstruct
    @Transactional
    public void initDefaultTemplates() {
        log.info("Инициализация системных шаблонов сообщений");

        List<MessageTemplate> defaultTemplates = Arrays.asList(
                createTemplate("👋 Приветствие", "Привет! Как у тебя дела?", 1, true),
                createTemplate("💚 Всё хорошо", "У меня всё хорошо. А у тебя?", 2, true),
                createTemplate("🏠 Дома", "Я сейчас дома. Как ты?", 3, true),
                createTemplate("💊 Лекарства", "Не забудь принять лекарства!", 4, true),
                createTemplate("🍽️ Еда", "Ты уже поел?", 5, true),
                createTemplate("🌙 Спокойной ночи", "Спокойной ночи! Сладких снов.", 6, true),
                createTemplate("☀️ Доброе утро", "Доброе утро! Хорошего дня!", 7, true),
                createTemplate("🆘 Помощь", "Мне нужна помощь. Перезвони, пожалуйста.", 8, true),
                createTemplate("❤️ Люблю", "Я тебя люблю!", 9, true),
                createTemplate("🤔 Вопрос", "У меня вопрос. Когда сможешь поговорить?", 10, true)
        );

        for (MessageTemplate template : defaultTemplates) {
            try {
                // Проверяем, существует ли уже такой шаблон (по названию и системному флагу)
                boolean exists = templateRepository.findByIsDefaultTrueOrderBySortOrderAscTitleAsc()
                        .stream()
                        .anyMatch(t -> t.getTitle().equals(template.getTitle()));

                if (!exists) {
                    templateRepository.save(template);
                    log.debug("✅ Создан системный шаблон: {}", template.getTitle());
                } else {
                    log.debug("⏩ Системный шаблон уже существует: {}", template.getTitle());
                }
            } catch (Exception e) {
                log.error("❌ Ошибка при создании шаблона {}: {}", template.getTitle(), e.getMessage());
            }
        }

        log.info("✅ Инициализация системных шаблонов завершена");
    }

    /**
     * Получить все шаблоны, доступные пользователю
     *
     * @param userId ID пользователя
     * @return список шаблонов
     */
    @Transactional(readOnly = true)
    public List<MessageTemplateResponseDto> getUserTemplates(UUID userId) {
        log.info("Получение шаблонов для пользователя {}", userId);

        return templateRepository.findUserTemplates(userId)
                .stream()
                .map(MessageTemplateResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Создать новый пользовательский шаблон
     *
     * @param userId ID пользователя
     * @param requestDto DTO с данными шаблона
     * @return созданный шаблон
     * @throws EntityNotFoundException если пользователь не найден
     */
    public MessageTemplateResponseDto createTemplate(UUID userId, MessageTemplateRequestDto requestDto) {
        log.info("Создание нового шаблона для пользователя {}", userId);

        User user = findUserById(userId);

        MessageTemplate template = new MessageTemplate();
        template.setUser(user);
        template.setTitle(requestDto.getTitle().trim());
        template.setContent(requestDto.getContent().trim());
        template.setSortOrder(requestDto.getSortOrder());
        template.setDefault(false);

        MessageTemplate saved = templateRepository.save(template);
        log.info("✅ Шаблон {} создан для пользователя {}", saved.getId(), userId);

        return MessageTemplateResponseDto.fromEntity(saved);
    }

    /**
     * Обновить существующий шаблон
     *
     * @param userId ID пользователя
     * @param templateId ID шаблона
     * @param requestDto новые данные
     * @return обновлённый шаблон
     * @throws EntityNotFoundException если шаблон не найден
     * @throws AccessDeniedException если это системный шаблон или шаблон другого пользователя
     */
    public MessageTemplateResponseDto updateTemplate(UUID userId, UUID templateId,
                                                     MessageTemplateRequestDto requestDto) {
        log.info("Обновление шаблона {} для пользователя {}", templateId, userId);

        MessageTemplate template = findTemplateById(templateId);

        // Проверяем права на редактирование
        if (template.isSystemTemplate()) {
            log.warn("Пользователь {} попытался отредактировать системный шаблон {}", userId, templateId);
            throw new AccessDeniedException("Нельзя редактировать системные шаблоны");
        }

        if (!template.getUser().getId().equals(userId)) {
            log.warn("Пользователь {} попытался отредактировать чужой шаблон {}", userId, templateId);
            throw new AccessDeniedException("Вы не можете редактировать этот шаблон");
        }

        template.setTitle(requestDto.getTitle().trim());
        template.setContent(requestDto.getContent().trim());
        if (requestDto.getSortOrder() != null) {
            template.setSortOrder(requestDto.getSortOrder());
        }

        MessageTemplate updated = templateRepository.save(template);
        log.info("✅ Шаблон {} обновлён", templateId);

        return MessageTemplateResponseDto.fromEntity(updated);
    }

    /**
     * Удалить шаблон
     *
     * @param userId ID пользователя
     * @param templateId ID шаблона
     * @throws EntityNotFoundException если шаблон не найден
     * @throws AccessDeniedException если это системный шаблон или шаблон другого пользователя
     */
    public void deleteTemplate(UUID userId, UUID templateId) {
        log.info("Удаление шаблона {} пользователем {}", templateId, userId);

        MessageTemplate template = findTemplateById(templateId);

        // Проверяем права на удаление
        if (template.isSystemTemplate()) {
            log.warn("Пользователь {} попытался удалить системный шаблон {}", userId, templateId);
            throw new AccessDeniedException("Нельзя удалять системные шаблоны");
        }

        if (!template.getUser().getId().equals(userId)) {
            log.warn("Пользователь {} попытался удалить чужой шаблон {}", userId, templateId);
            throw new AccessDeniedException("Вы не можете удалить этот шаблон");
        }

        templateRepository.delete(template);
        log.info("✅ Шаблон {} удалён", templateId);
    }

    /**
     * Создать шаблон (вспомогательный метод для инициализации)
     */
    private MessageTemplate createTemplate(String title, String content, int sortOrder, boolean isDefault) {
        MessageTemplate template = new MessageTemplate();
        // Не задаём ID вручную, пусть генерируется автоматически
        template.setTitle(title);
        template.setContent(content);
        template.setSortOrder(sortOrder);
        template.setDefault(isDefault);
        return template;
    }

    /**
     * Найти шаблон по ID
     *
     * @param templateId ID шаблона
     * @return сущность шаблона
     * @throws EntityNotFoundException если шаблон не найден
     */
    private MessageTemplate findTemplateById(UUID templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Шаблон с ID '%s' не найден", templateId)
                ));
    }

    /**
     * Найти пользователя по ID
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
}
