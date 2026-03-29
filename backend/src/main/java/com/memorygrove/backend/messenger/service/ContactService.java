package com.memorygrove.backend.messenger.service;

import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.messenger.entity.dbo.TrustedContact;
import com.memorygrove.backend.messenger.entity.dto.request.AddContactRequestDto;
import com.memorygrove.backend.messenger.entity.dto.request.UpdateContactRequestDto;
import com.memorygrove.backend.messenger.entity.dto.response.ContactResponseDto;
import com.memorygrove.backend.messenger.repository.TrustedContactRepository;
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
 * Сервис для работы с доверенными контактами пользователей
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Добавления новых контактов</li>
 *   <li>Просмотра списка контактов</li>
 *   <li>Обновления настроек контактов</li>
 *   <li>Удаления контактов</li>
 *   <li>Проверки прав на общение</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final TrustedContactRepository contactRepository;
    private final UserRepository userRepository;

    /**
     * Добавить новый доверенный контакт
     *
     * @param userId ID пользователя, который добавляет контакт
     * @param requestDto DTO с данными для добавления
     * @return добавленный контакт
     * @throws IllegalArgumentException если пользователь пытается добавить самого себя
     * @throws IllegalStateException если контакт уже существует
     * @throws EntityNotFoundException если пользователь с указанным ID не найден
     */
    public ContactResponseDto addContact(UUID userId, AddContactRequestDto requestDto) {
        log.info("Добавление нового контакта для пользователя: {}", userId);

        UUID contactId = UUID.fromString(requestDto.getContactId());

        // Проверяем, не добавляет ли пользователь сам себя
        if (userId.equals(contactId)) {
            log.warn("Пользователь {} попытался добавить сам себя в контакты", userId);
            throw new IllegalArgumentException("Нельзя добавить самого себя в контакты");
        }

        // Проверяем, существует ли уже такой контакт
        if (contactRepository.existsByUserIdAndContactId(userId, contactId)) {
            log.warn("Контакт {} уже существует для пользователя {}", contactId, userId);
            throw new IllegalStateException("Этот пользователь уже добавлен в контакты");
        }

        // Находим пользователей
        User user = findUserById(userId);
        User contact = findUserById(contactId);

        // Создаём новую связь
        TrustedContact trustedContact = new TrustedContact();
        trustedContact.setUser(user);
        trustedContact.setContact(contact);
        trustedContact.setNickname(requestDto.getNickname());

        TrustedContact saved = contactRepository.save(trustedContact);
        log.info("✅ Контакт {} успешно добавлен для пользователя {}", contactId, userId);

        return ContactResponseDto.fromEntity(saved);
    }

    /**
     * Получить все активные контакты пользователя
     *
     * @param userId ID пользователя
     * @return список доверенных контактов
     */
    @Transactional(readOnly = true)
    public List<ContactResponseDto> getActiveContacts(UUID userId) {
        log.info("Получение активных контактов для пользователя: {}", userId);

        return contactRepository.findByUserIdAndIsActiveTrueOrderByAddedAtDesc(userId)
                .stream()
                .map(ContactResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получить все контакты пользователя (включая неактивные)
     *
     * @param userId ID пользователя
     * @return список всех контактов
     */
    @Transactional(readOnly = true)
    public List<ContactResponseDto> getAllContacts(UUID userId) {
        log.info("Получение всех контактов для пользователя: {}", userId);

        return contactRepository.findByUserIdOrderByAddedAtDesc(userId)
                .stream()
                .map(ContactResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получить информацию о конкретном контакте
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     * @return информация о контакте
     * @throws EntityNotFoundException если контакт не найден
     */
    @Transactional(readOnly = true)
    public ContactResponseDto getContact(UUID userId, UUID contactId) {
        log.info("Получение информации о контакте {} для пользователя {}", contactId, userId);

        TrustedContact contact = findContact(userId, contactId);
        return ContactResponseDto.fromEntity(contact);
    }

    /**
     * Обновить настройки контакта
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     * @param requestDto DTO с новыми настройками
     * @return обновлённый контакт
     * @throws EntityNotFoundException если контакт не найден
     */
    public ContactResponseDto updateContact(UUID userId, UUID contactId, UpdateContactRequestDto requestDto) {
        log.info("Обновление настроек контакта {} для пользователя {}", contactId, userId);

        TrustedContact contact = findContact(userId, contactId);

        if (requestDto.getNickname() != null) {
            contact.setNickname(requestDto.getNickname());
        }
        if (requestDto.getIsActive() != null) {
            contact.setActive(requestDto.getIsActive());
        }
        if (requestDto.getCanSendMessages() != null) {
            contact.setCanSendMessages(requestDto.getCanSendMessages());
        }
        if (requestDto.getCanReceiveNotifications() != null) {
            contact.setCanReceiveNotifications(requestDto.getCanReceiveNotifications());
        }

        TrustedContact updated = contactRepository.save(contact);
        log.info("✅ Настройки контакта {} обновлены", contactId);

        return ContactResponseDto.fromEntity(updated);
    }

    /**
     * Удалить контакт
     *
     * @param userId ID пользователя
     * @param contactId ID контакта для удаления
     * @throws EntityNotFoundException если контакт не найден
     */
    public void deleteContact(UUID userId, UUID contactId) {
        log.info("Удаление контакта {} для пользователя {}", contactId, userId);

        TrustedContact contact = findContact(userId, contactId);
        contactRepository.delete(contact);

        log.info("✅ Контакт {} успешно удалён", contactId);
    }

    /**
     * Проверить, могут ли два пользователя общаться
     *
     * @param user1Id ID первого пользователя
     * @param user2Id ID второго пользователя
     * @return true если общение разрешено
     */
    @Transactional(readOnly = true)
    public boolean canCommunicate(UUID user1Id, UUID user2Id) {
        if (user1Id.equals(user2Id)) {
            return false; // Нельзя общаться с самим собой
        }

        return contactRepository.isActiveContact(user1Id, user2Id) ||
                contactRepository.isActiveContact(user2Id, user1Id);
    }

    /**
     * Получить все контакты, которым нужно отправлять уведомления
     *
     * @param userId ID пользователя
     * @return список контактов
     */
    @Transactional(readOnly = true)
    public List<TrustedContact> getContactsForNotifications(UUID userId) {
        return contactRepository.findByUserIdAndCanReceiveNotificationsTrueAndIsActiveTrue(userId);
    }

    /**
     * Найти контакт по ID с проверкой принадлежности пользователю
     *
     * @param userId ID пользователя
     * @param contactId ID контакта
     * @return сущность доверенного контакта
     * @throws EntityNotFoundException если контакт не найден
     */
    private TrustedContact findContact(UUID userId, UUID contactId) {
        return contactRepository.findByUserIdAndContactId(userId, contactId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Контакт с ID '%s' не найден у пользователя '%s'", contactId, userId)
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
