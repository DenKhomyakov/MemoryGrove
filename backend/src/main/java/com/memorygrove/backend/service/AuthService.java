package com.memorygrove.backend.service;

import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.auth.request.LoginRequestDto;
import com.memorygrove.backend.entity.dto.auth.request.RefreshTokenRequestDto;
import com.memorygrove.backend.entity.dto.auth.response.JwtResponseDto;
import com.memorygrove.backend.repository.UserRepository;
import com.memorygrove.backend.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис для аутентификации и управления JWT токенами.
 * Предоставляет функционал:
 * - Аутентификация пользователя и генерация JWT токенов
 * - Обновление access токена по refresh токену
 * - Выход из системы
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Аутентифицирует пользователя и генерирует пару JWT токенов
     * Процесс аутентификации:
     * - Поиск пользователя по идентификатору
     * - Проверка пароля
     * - Аутентификация через AuthenticationManager
     * - Обновление времени последнего входа
     * - Генерация access и refresh токенов
     *
     * @param dto DTO с данными для входа (идентификатор и пароль)
     * @return DTO с access и refresh токенами
     * @throws IllegalArgumentException если учетные данные неверны
     */
    public JwtResponseDto authenticate(LoginRequestDto dto) {
        log.info("Аутентификация пользователя с идентификатором {}", dto.getIdentifier());

        try {
            User user = findByIdentifier(dto.getIdentifier());
            log.info("Найден пользователь: {}, хеш пароля в БД: {}",
                    user.getEmail(), user.getPasswordHash());

            boolean matches = passwordEncoder.matches(dto.getPassword(), user.getPasswordHash());
            log.info("Пароль совпадает (прямая проверка): {}", matches);

            if (!matches) {
                throw new BadCredentialsException("Неверный пароль");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getIdentifier(),
                            dto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails, user.getId());

            log.info("Пользователь с идентификатором {} успешно аутентифицирован", user.getEmail());

            return new JwtResponseDto(accessToken, refreshToken, "Bearer");

        } catch (BadCredentialsException e) {
            log.error("Неверный пароль для пользователя {}", dto.getIdentifier());
            throw new IllegalArgumentException("Неверные учетные данные");
        } catch (AuthenticationException e) {
            log.error("Ошибка аутентификации: {}", e.getMessage());
            throw new IllegalArgumentException("Неверные учетные данные");
        } catch (Exception e) {
            log.error("Неожиданная ошибка: ", e);
            throw new IllegalArgumentException("Ошибка аутентификации");
        }
    }

    /**
     * Обновляет access токен с помощью refresh токена
     * Используется когда access токен истек, но refresh токен еще действителен
     *
     * @param dto DTO с refresh токеном
     * @return DTO с новыми access и refresh токенами
     * @throws IllegalArgumentException если refresh токен недействителен
     */
    public JwtResponseDto refreshToken(RefreshTokenRequestDto dto) {
        log.info("Обновление токена");

        String refreshToken = dto.getRefreshToken();

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            log.warn("Попытка обновления с использованием не refresh токена");
            throw new IllegalArgumentException("Токен не является refresh токеном");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UUID userId = jwtUtil.extractUserId(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            log.warn("Недействительный refresh токен для пользователя: {}", username);
            throw new IllegalArgumentException("Недействительный refresh токен");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userDetails, userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, userId);

        log.info("Токены успешно обновлены для пользователя: {}", username);

        return new JwtResponseDto(newAccessToken, newRefreshToken, "Bearer");
    }

    /**
     * Выполняет выход пользователя из системы
     * Очищает контекст безопасности и может добавить токен в черный список
     *
     * @param token JWT токен для выхода (может использоваться для добавления токена в черный список)
     */
    public void logout(String token) {
        log.info("Выход пользователя из системы");
        SecurityContextHolder.clearContext();

        // ToDo: Здесь можно добавить логику для добавления токена в черный список (TokenBlacklistService)
        //tokenBlacklistService.addToBlacklist(token);
    }

    /**
     * Поиск пользователя по идентификатору (email или телефон)
     *
     * @param identifier email или телефон пользователя
     * @return сущность пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */
    private User findByIdentifier(String identifier) {
        return userRepository.findByEmailOrPhone(identifier)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Пользователь с идентификатором '%s' не найден", identifier)
                        )
                );
    }
}
