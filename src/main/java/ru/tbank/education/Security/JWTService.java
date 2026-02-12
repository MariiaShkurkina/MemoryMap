package ru.tbank.education.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    private final SecretKey key;
    private final long expirationMs;

    public JWTService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expirationMinutes}") long expirationMinutes
    ) {
        // ключ для подписи (HS256)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));//Преобразование строки-секрета в криптографический ключ.
        this.expirationMs = expirationMinutes * 60_000L;//Преобразование минут в миллисекунды
    }

    // Создаём токен: subject=id, claim role=USER/ADMIN
    public String generateToken(Long userId, String role) {
        Date now = new Date();//Текущее время
        Date exp = new Date(now.getTime() + expirationMs);// Время истечения

        return Jwts.builder()
                .subject(String.valueOf(userId))//хранит идентификатор пользователя
                .claim("role", role)//роль
                .issuedAt(now)//время создания
                .expiration(exp)//время истечения
                .signWith(key)//Подпись токена секретным ключом
                .compact();
    }

    // Проверка: подпись верная и не истёк срок
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        String sub = parse(token).getPayload().getSubject();
        return Long.parseLong(sub);
    }

    public String extractRole(String token) {
        Object role = parse(token).getPayload().get("role");
        return role == null ? null : role.toString();
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}