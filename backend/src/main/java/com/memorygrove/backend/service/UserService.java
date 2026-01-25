package com.memorygrove.backend.service;

import com.memorygrove.backend.dictionary.Role;
import com.memorygrove.backend.dictionary.Sex;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.user.request.UserCreateDto;
import com.memorygrove.backend.entity.dto.user.request.UserDeactivationDto;
import com.memorygrove.backend.entity.dto.user.request.UserPasswordChangeDto;
import com.memorygrove.backend.entity.dto.user.request.UserUpdateDto;
import com.memorygrove.backend.entity.dto.user.response.UserResponseDto;
import com.memorygrove.backend.repository.RoleRepository;
import com.memorygrove.backend.repository.SexRepository;
import com.memorygrove.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис для работы с пользователем
 * - Регистрация новых пользователей
 * - Обновление данных пользователей
 * - Смена пароля
 * - Деактивация / активация аккаунтов
 * - Поиск и получение информации о пользователях
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final SexRepository sexRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрация нового пользователя
     * @param userCreateDto DTO с данными для регистрации нового пользователя
     * @return DTO с информацией о созданном пользователе
     * @throws IllegalArgumentException если данные некорректны
     */
    @Transactional
    public UserResponseDto register(UserCreateDto userCreateDto) {
        log.info("Начало регистрации пользователя с электронной почтой {}", userCreateDto.getEmail());

        validateUniquenessEmailAndNumber(userCreateDto.getEmail(), userCreateDto.getPhone());

        if (!userCreateDto.getPassword().equals(userCreateDto.getConfirmPassword())) {
            log.warn("Введённые пароли не совпадают");
            throw new IllegalArgumentException("Введённые пароли не совпадают");
        }

        Sex sex = findSexById(userCreateDto.getSex().getId());
        Role role = findRoleById(userCreateDto.getRole().getId());

        User user = createUserFromDto(userCreateDto, sex, role);
        User savedUser = userRepository.save(user);
        log.info(
                "Пользователь успешно зарегистрирован!"
                        + "\nID: {}"
                        + "\nЭлектронная почта: {}",
                savedUser.getId(),
                savedUser.getEmail()
        );

        return UserResponseDto.fromEntity(savedUser);
    }

    /**
     * Получение пользователя по ID
     * @param userId UUID пользователя
     * @return DTO с информацией о пользователе
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Transactional(readOnly = true)
    public UserResponseDto getById(UUID userId) {
        log.info("Поиск пользователя с ID {}", userId);
        User user = findUserById(userId);

        return UserResponseDto.fromEntity(user);
    }

    /**
     * Получение пользователя по электронной почте
     * @param email электронная почта пользователя
     * @return DTO с информацией о пользователе
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Transactional(readOnly = true)
    public UserResponseDto getByEmail(String email) {
        log.info("Поиск пользователя с электронной почтой {}", email);

        User user = findUserByEmail(email);
        return UserResponseDto.fromEntity(user);
    }

    /**
     * Обновление данных пользователя
     * @param userId UUID пользователя
     * @param dto DTO с новыми данными
     * @return DTO с обновлённой информацией о пользователе
     * @throws IllegalArgumentException если данные некорректны
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Transactional
    public UserResponseDto update(UUID userId, UserUpdateDto dto) {
        log.info("Обновление данных пользователя с ID {}", userId);

        User user = findUserById(userId);

        if (
                !user.getPhone().equals(dto.getPhone())
                        && userRepository.existsByPhone(dto.getPhone())
        ) {
            log.warn("Номер телефона {} уже занят другим пользователем", dto.getPhone());
            throw new IllegalArgumentException(
                    String.format("Номер телефона '%s' уже занят другим пользователем", dto.getPhone())
            );
        }

        Sex sex = findSexById(dto.getSex().getId());
        updateUserFromDto(user, dto, sex);

        User updateUser = userRepository.save(user);
        log.info("Данные пользователя с ID {} успешно обновлены", userId);

        return UserResponseDto.fromEntity(updateUser);
    }

    /**
     * Смена пароля пользователя
     * @param userId UUID пользователя
     * @param dto DTO с данными для смены пароля
     * @throws IllegalArgumentException если пароли не совпадают
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Transactional
    public void changePassword(UUID userId, UserPasswordChangeDto dto) {
        log.info("Смена пароля для пользователя с ID {}", userId);

        User user = findUserById(userId);

        // ToDo: Убедиться, что проверка работает корректно (есть подозрение, что нужно кодировать пароль из dto перед проверкой)
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("Неверный текущий пароль для пользователя с ID {}", userId);
            throw new IllegalArgumentException("Неверный текущий пароль");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            log.warn("Введённые новые пароли не совпадают для пользователя с ID {}", userId);
            throw new IllegalArgumentException("Введённые новые пароли не совпадают");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Пароль успешно изменён для пользователя с ID {}", userId);
    }

    /**
     * Деактивация пользователя
     * @param userId UUID пользователя
     * @param dto DTO с данными для деактивации
     * @throws EntityNotFoundException если пользователь не найден
     * @throws IllegalStateException если пользователь уже деактивирован
     */
    @Transactional
    public void deactivate(UUID userId, UserDeactivationDto dto) {
        log.info("Деактивация пользователя с ID {}", userId);

        User user = findUserById(userId);

        if (!user.isActive()) {
            log.warn("Пользователь с ID {} уже деактивирован", userId);
            throw new IllegalStateException("Пользователь уже деактивирован");
        }

        user.setActive(false);
        user.setDeactivatedAt(
                dto.getDeactivatedAt() != null
                        ? dto.getDeactivatedAt()
                        : LocalDateTime.now()
        );
        user.setDeactivationReason(dto.getReason());

        userRepository.save(user);
        log.info("Пользователь с ID {} успешно деактивирован", userId);
    }

    /**
     * Активация пользователя
     * @param userId UUID пользователя
     * @throws EntityNotFoundException если пользователь не найден
     * @throws IllegalStateException если пользователь уже активирован
     */
    public void activate(UUID userId) {
        log.info("Активация пользователя с ID {}", userId);

        User user = findUserById(userId);

        if (user.isActive()) {
            log.warn("Пользователь с ID {} уже активен", userId);
            throw new IllegalStateException("Пользователь уже активен");
        }

        user.setActive(true);
        user.setDeactivatedAt(null);
        user.setDeactivationReason(null);

        userRepository.save(user);
        log.info("Пользователь с ID {} успешно активирован", userId);
    }

    /**
     * Проверка уникальности электронной почты и номера телефона пользователя
     * @param email электронная почта пользователя
     * @param phone номер телефона пользователя
     */
    private void validateUniquenessEmailAndNumber(String email, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь с указанной электронной почтой ('%s') уже существует", email)
            );
        }

        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь с указанным номером телефона ('%s') уже существует", phone)
            );
        }
    }

    /**
     * Поиск пользователя по ID
     * @param userId UUID пользователя
     * @return пользователь
     */
    private User findUserById(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Пользователь с ID '%s' не найден", userId)
                        )
                );
    }

    /**
     * Поиск пользователя по электронной почте
     * @param email электронная почта пользователя
     * @return пользователь
     */
    private User findUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Пользователь с электронной почтой '%s' не найден", email)
                        )
                );
    }

    /**
     * Поиск пола по ID
     * @param sexId UUID пола
     * @return значение пола
     */
    private Sex findSexById(UUID sexId) {
        return sexRepository
                .findById(sexId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Пол с ID '%s' не найден", sexId)
                        )
                );
    }

    /**
     * Поиск роли по ID
     * @param roleId UUID роли
     * @return значение роли
     */
    private Role findRoleById(UUID roleId) {
        return roleRepository
                .findById(roleId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Роль с ID '%s' не найдена", roleId)
                        )
                );
    }

    /**
     * Создание User из DTO (UserCreateDto)
     * @param dto DTO для создания сущности User
     * @param sex пол пользователя
     * @param role роль пользователя
     * @return пользователь
     */
    private User createUserFromDto(UserCreateDto dto, Sex sex, Role role) {
        User user = new User();

        user.setSurname(dto.getSurname());
        user.setName(dto.getName());
        user.setMiddleName(dto.getMiddleName());
        user.setBirthDate(dto.getBirthDate());
        user.setSex(sex);
        user.setRole(role);
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setDescription(dto.getDescription());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        return user;
    }

    /**
     * Обновление пользователя данными из DTO
     * @param user пользователь, для которого обновляются данные
     * @param dto DTO с данными для обновления
     * @param sex пол пользователя
     */
    private void updateUserFromDto(User user, UserUpdateDto dto, Sex sex) {
        user.setSurname(dto.getSurname());
        user.setName(dto.getName());
        user.setMiddleName(dto.getMiddleName());
        user.setBirthDate(dto.getBirthDate());
        user.setSex(sex);
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setDescription(dto.getDescription());
    }
}
