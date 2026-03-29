package com.memorygrove.backend.security;

import com.memorygrove.backend.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для обработки JWT токенов в каждом HTTP запросе.
 * Выполняет следующие функции:
 * - Извлекает JWT токен из заголовка Authorization
 * - Проверяет валидность токена
 * - Загружает данные пользователя
 * - Устанавливает аутентификацию в SecurityContext
 *
 * Фильтр наследуется от OncePerRequestFilter, что гарантирует однократное выполнение
 * для каждого запроса
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Основной метод фильтрации, выполняемый для каждого запроса
     * Извлекает токен, проверяет его и устанавливает аутентификацию
     *
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException если происходит ошибка сервлета
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("Обработка запроса: {} {}", request.getMethod(), requestURI);

        try {
            String jwt = parseJwt(request);

            if (jwt == null) {
                log.debug("JWT токен отсутствует в запросе");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("JWT токен найден, начинаем валидацию");

            try {
                String username = jwtUtil.extractUsername(jwt);
                log.debug("Извлечен username из токена: {}", username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.debug("Загружаем UserDetails для пользователя: {}", username);

                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        log.debug("UserDetails загружен. Роли: {}", userDetails.getAuthorities());

                        if (jwtUtil.validateToken(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            log.info("✅ Пользователь {} успешно аутентифицирован для доступа к {}",
                                    username, requestURI);
                        } else {
                            log.warn("❌ Токен не прошел валидацию для пользователя {}", username);
                        }
                    } catch (UsernameNotFoundException e) {
                        log.error("Пользователь не найден: {}", username);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.error("❌ JWT токен истек: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.error("❌ Неподдерживаемый JWT токен: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                log.error("❌ Некорректный JWT токен: {}", e.getMessage());
            } catch (SignatureException e) {
                log.error("❌ Ошибка подписи JWT токена: {}", e.getMessage());
            } catch (Exception e) {
                log.error("❌ Ошибка при обработке JWT токена: {}", e.getMessage(), e);
            }

        } catch (Exception e) {
            log.error("Неожиданная ошибка в JWT фильтре: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT токен из заголовка Authorization
     * Ожидается формат заголовка: "Bearer {token}"
     *
     * @param request HTTP запрос
     * @return JWT токен или null, если токен отсутствует или имеет неверный формат
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        log.debug("Authorization header: {}", headerAuth != null ? "присутствует" : "отсутствует");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            log.debug("JWT токен извлечен из заголовка, длина: {}", token.length());
            return token;
        }

        return null;
    }
}
