package com.memorygrove.backend.util;

import com.memorygrove.backend.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Утилитарный класс для работы с JWT (JSON Web Token)
 * Предоставляет методы для:
 * - Генерации access и refresh токенов
 * - Извлечения данных из токенов (username, userId)
 * - Валидации токенов
 * - Проверки типа токена (access/refresh)
 *
 * @see com.memorygrove.backend.config.JwtProperties
 * @see io.jsonwebtoken.Jwts
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey signingKey;

    /**
     * Инициализирует компонент после создания бина
     * Создает ключ подписи на основе секретного ключа из конфигурации
     */
    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecretKey();
        log.info("JWT Secret Key длина: {} символов", secret != null ? secret.length() : "null");
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        log.info("JWT инициализирован. AccessToken expiration: {}, RefreshToken expiration: {}",
                jwtProperties.getAccessTokenExpirationMs(),
                jwtProperties.getRefreshTokenExpirationMs());
    }

    /**
     * Возвращает ключ для подписи токенов
     *
     * @return секретный ключ для подписи JWT
     */
    private SecretKey getSigningKey() {
        return signingKey;
    }

    /**
     * Извлекает имя пользователя (subject) из токена
     *
     * @param token JWT токен
     * @return имя пользователя
     * @throws JwtException если токен невалиден
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("Ошибка извлечения username из токена: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Извлекает ID пользователя из токена
     *
     * @param token JWT токен
     * @return UUID пользователя
     * @throws JwtException если токен невалиден или userId отсутствует
     */
    public UUID extractUserId(String token) {
        try {
            String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
            return UUID.fromString(userIdStr);
        } catch (Exception e) {
            log.error("Ошибка извлечения userId из токена: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Извлекает дату истечения токена
     *
     * @param token JWT токен
     * @return дата истечения
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает произвольные данные из токена
     *
     * @param token JWT токен
     * @param claimsResolver функция для извлечения конкретных данных
     * @param <T> тип извлекаемых данных
     * @return извлеченные данные
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все claims из токена
     *
     * @param token JWT токен
     * @return объект Claims со всеми данными
     * @throws ExpiredJwtException если токен истек
     * @throws UnsupportedJwtException если тип токена не поддерживается
     * @throws MalformedJwtException если токен имеет некорректный формат
     * @throws SignatureException если подпись токена недействительна
     * @throws IllegalArgumentException если токен пустой или null
     */
    private Claims extractAllClaims(String token) {
        try {
            log.debug("Парсинг токена: {}", token.substring(0, Math.min(20, token.length())) + "...");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("Токен успешно распарсен. Subject: {}, IssuedAt: {}, Expiration: {}",
                    claims.getSubject(), claims.getIssuedAt(), claims.getExpiration());
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("Токен истек: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("Неподдерживаемый JWT токен: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Некорректный JWT токен: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.error("Ошибка подписи JWT токена: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Пустой или null JWT токен: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неизвестная ошибка при парсинге JWT: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Проверяет, истек ли токен
     *
     * @param token JWT токен
     * @return true если токен истек, false если еще действителен
     */
    private Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            boolean expired = expiration.before(new Date());
            if (expired) {
                log.debug("Токен истек. Expiration: {}, Current: {}", expiration, new Date());
            }
            return expired;
        } catch (Exception e) {
            log.error("Ошибка проверки истечения токена: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Генерирует access токен для пользователя
     *
     * @param userDetails данные пользователя
     * @param userId UUID пользователя
     * @return сгенерированный access токен
     */
    public String generateAccessToken(UserDetails userDetails, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        String token = createToken(claims, userDetails.getUsername(), jwtProperties.getAccessTokenExpirationMs());
        log.debug("Сгенерирован access token для пользователя: {}, userId: {}", userDetails.getUsername(), userId);
        return token;
    }

    /**
     * Генерирует refresh токен для пользователя
     *
     * @param userDetails данные пользователя
     * @param userId UUID пользователя
     * @return сгенерированный refresh токен
     */
    public String generateRefreshToken(UserDetails userDetails, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("type", "refresh");
        String token = createToken(claims, userDetails.getUsername(), jwtProperties.getRefreshTokenExpirationMs());
        log.debug("Сгенерирован refresh token для пользователя: {}, userId: {}", userDetails.getUsername(), userId);
        return token;
    }

    /**
     * Создает JWT токен с указанными claims, subject и временем жизни
     *
     * @param claims данные для включения в токен
     * @param subject имя пользователя (subject)
     * @param expiration время жизни в миллисекундах
     * @return сгенерированный JWT токен
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        log.debug("Создан токен. Subject: {}, Expiration: {}", subject, new Date(System.currentTimeMillis() + expiration));
        return token;
    }

    /**
     * Проверяет валидность токена для указанного пользователя
     *
     * @param token JWT токен
     * @param userDetails данные пользователя для проверки
     * @return true если токен валиден, false в противном случае
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            log.debug("Валидация токена для пользователя {}: {}", username, isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Ошибка валидации токена: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет, является ли токен refresh токеном
     *
     * @param token JWT токен
     * @return true если это refresh токен, false в противном случае
     */
    public Boolean isRefreshToken(String token) {
        try {
            String type = extractClaim(token, claims -> claims.get("type", String.class));
            boolean isRefresh = "refresh".equals(type);
            log.debug("Проверка является ли токен refresh: {}", isRefresh);
            return isRefresh;
        } catch (Exception e) {
            log.debug("Токен не является refresh (или ошибка парсинга)");
            return false;
        }
    }
}
