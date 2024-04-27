package ru.dolya.t1_security.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dolya.t1_security.model.RoleType;
import ru.dolya.t1_security.model.dto.AuthResponseDto;
import ru.dolya.t1_security.model.entity.UserEntity;
import ru.dolya.t1_security.secutiry.JwtService;
import ru.dolya.t1_security.secutiry.UserPrincipal;
import ru.dolya.t1_security.service.AuthService;
import ru.dolya.t1_security.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponseDto auth(UserPrincipal userPrincipal) {
        List<RoleType> roles = userPrincipal.getAuthorities().stream()
                .map(authority -> RoleType.valueOf(authority.getAuthority()))
                .toList();
        String token = jwtService.generateJwt(userPrincipal.getUsername(), roles);
        String refreshToken = jwtService.createRefreshToken(userPrincipal.getUsername());
        return new AuthResponseDto()
                .setAccessToken(token)
                .setRefreshToken(refreshToken);
    }

    @Override
    @Transactional
    public AuthResponseDto refresh(String refreshToken) {
        log.info("Запрос на обновление токена");
        UserEntity userEntity = userService.findByRefreshToken(refreshToken);
        String token = userEntity.getRefreshToken();
        LocalDateTime expirationDateToken = userEntity.getExpirationDateToken();
        if (LocalDateTime.now().isBefore(expirationDateToken)) {
            String accessToken = jwtService.generateJwt(userEntity.getUsername(), userEntity.getRoles());
            return new AuthResponseDto()
                    .setAccessToken(accessToken)
                    .setRefreshToken(token);
        } else {
            throw new RuntimeException("Срок действия refresh-token истек. Нужно залогниться вновь");
        }
    }
}
