package ru.dolya.t1_security.secutiry;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dolya.t1_security.model.RoleType;
import ru.dolya.t1_security.model.entity.UserEntity;
import ru.dolya.t1_security.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.tokenExpiration}")
    private long accessTokenLifetime;
    @Value("${jwt.refreshTokenExpiration}")
    private Long refreshTokenLifetime;


    public String generateJwt(String username, List<RoleType> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("username", username);
        Date issuedTime = new Date();
        Date expiredTime = new Date(issuedTime.getTime() + accessTokenLifetime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedTime)
                .setExpiration(expiredTime)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public UserPrincipal parseJwt(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        String username = claims.get("username", String.class);
        return UserPrincipal.builder()
                .username(username)
                .authorities(extractAuthorities(claims))
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Transactional
    public String createRefreshToken(String username) {
        UserEntity userEntity = userService.findByUsername(username);
        userEntity.setExpirationDateToken(LocalDateTime.now().plusSeconds(refreshTokenLifetime / 1000));
        String refreshToken = UUID.randomUUID().toString();
        userEntity.setRefreshToken(refreshToken);
        userService.save(userEntity);
        return refreshToken;
    }

}
