package com.memorygrove.backend.config;

import com.memorygrove.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Основная конфигурация безопасности приложения
 * Настраивает:
 * - Отключение CSRF (для stateless REST API)
 * - Stateless сессии (без сохранения состояния)
 * - Права доступа к эндпоинтам (permitAll/hasRole/authenticated)
 * - JWT аутентификацию через фильтр
 * - Провайдер аутентификации с BCrypt
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Создает цепочку фильтров безопасности
     * Определяет:
     * - Какие эндпоинты общедоступны (permitAll)
     * - Какие требуют определенных ролей (hasRole)
     * - Какие требуют аутентификации (authenticated)
     * - Порядок применения фильтров
     *
     * @param http объект для настройки HTTP безопасности
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception если возникает ошибка при настройке
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-resources/**"
                        ).permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Создает провайдер аутентификации на основе DAO
     * Связывает UserDetailsService (загрузку пользователей) с PasswordEncoder (проверку паролей)
     *
     * @return настроенный AuthenticationProvider
     * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        // Важно: явно указываем, что хотим скрыть исключение "UserNotFound"
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    /**
     * Создает менеджер аутентификации
     * Используется для обработки запросов на аутентификацию
     *
     * @param authenticationConfiguration конфигурация аутентификации
     * @return менеджер аутентификации
     * @throws Exception если возникает ошибка при получении менеджера
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
