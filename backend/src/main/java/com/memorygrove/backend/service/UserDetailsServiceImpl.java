package com.memorygrove.backend.service;

import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Реализация UserDetailsService для загрузки данных пользователя из БД
 * Преобразует сущность User в объект UserDetails, используемый Spring Security
 *
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see com.memorygrove.backend.entity.dbo.User
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает пользователя по идентификатору (email или телефон).
     * Выполняет следующие проверки:
     * - Существование пользователя в БД
     * - Активность аккаунта (isActive = true)
     *
     * @param identifier идентификатор пользователя (email или телефон)
     * @return объект UserDetails для Spring Security
     * @throws UsernameNotFoundException если пользователь не найден или деактивирован
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.info("Загрузка пользователя по идентификатору: {}", identifier);

        User user = userRepository.findByEmailOrPhone(identifier)
                .orElseThrow(() -> {
                    log.warn("Пользователь с идентификатором '{}' не найден", identifier);
                    return new UsernameNotFoundException(
                            String.format("Пользователь с идентификатором '%s' не найден", identifier)
                    );
                });

        if (!user.isActive()) {
            log.warn("Пользователь {} деактивирован", user.getEmail());
            throw new UsernameNotFoundException("Пользователь деактивирован");
        }

        log.info("Пользователь найден: {}, роль: {}, хеш пароля: {}",
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName() : "нет роли",
                user.getPasswordHash() != null ? "установлен" : "ОТСУТСТВУЕТ!");

        // Используем английское название роли для Spring Security
        String roleName = getRoleForSpringSecurity(user);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }

    /**
     * Преобразует русское название роли в английский формат для Spring Security
     * Так как Spring Security невосприимчив к кириллице, необходим маппинг
     *
     * @param user пользователь с русским названием роли
     * @return английское название роли в формате "ROLE_*"
     */
    private String getRoleForSpringSecurity(User user) {
        if (user.getRole() == null) {
            return "ROLE_USER";
        }

        // Маппинг русских названий ролей на английские
        return switch (user.getRole().getName()) {
            case "Пациент" -> "ROLE_PATIENT";
            case "Доктор" -> "ROLE_DOCTOR";
            case "Член семьи" -> "ROLE_FAMILY_MEMBER";
            case "Вторая половинка / партнёр" -> "ROLE_PARTNER";
            case "Друг" -> "ROLE_FRIEND";
            case "Коллега" -> "ROLE_COLLEAGUE";
            case "Администратор (суперпользователь)" -> "ROLE_ADMIN";
            default -> "ROLE_" + user.getRole().getName().toUpperCase().replace(" ", "_");
        };
    }
}
