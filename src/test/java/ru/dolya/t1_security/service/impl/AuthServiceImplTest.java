package ru.dolya.t1_security.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.dolya.t1_security.model.RoleType;
import ru.dolya.t1_security.model.dto.AuthResponseDto;
import ru.dolya.t1_security.model.entity.UserEntity;
import ru.dolya.t1_security.secutiry.JwtService;
import ru.dolya.t1_security.secutiry.UserPrincipal;
import ru.dolya.t1_security.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    UserService userService;

    @Mock
    JwtService jwtService;

    @InjectMocks
    AuthServiceImpl authService;

    @Test
    void auth() {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .username("testUser")
                .authorities(Stream.of(RoleType.USER)
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList()))
                .build();
        when(jwtService.generateJwt("testUser", Collections.singletonList(RoleType.USER))).thenReturn("newToken");
        when(jwtService.createRefreshToken("testUser")).thenReturn("newRefreshToken");

        AuthResponseDto responseDto = authService.auth(userPrincipal);

        assertNotNull(responseDto);
        assertEquals("newToken", responseDto.getAccessToken());
        assertEquals("newRefreshToken", responseDto.getRefreshToken());
    }

    @Test
    void refresh() {
        String refreshToken = "testRefreshToken";
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");
        userEntity.setRoles(Collections.emptyList());
        userEntity.setRefreshToken("testRefreshToken");
        userEntity.setExpirationDateToken(LocalDateTime.now().plusDays(1));

        when(userService.findByRefreshToken("testRefreshToken")).thenReturn(userEntity);
        when(jwtService.generateJwt("testUser", Collections.emptyList())).thenReturn("newAccessToken");

        AuthResponseDto responseDto = authService.refresh(refreshToken);

        assertNotNull(responseDto);
        assertEquals("newAccessToken", responseDto.getAccessToken());
        assertEquals("testRefreshToken", responseDto.getRefreshToken());
    }

    @Test
    void refreshThrowException() {
        String expiredRefreshToken = "expiredRefreshToken";
        UserEntity userEntity = new UserEntity();
        userEntity.setExpirationDateToken(LocalDateTime.now().minusDays(1));

        when(userService.findByRefreshToken(expiredRefreshToken)).thenReturn(userEntity);

        assertThrows(RuntimeException.class, () -> authService.refresh(expiredRefreshToken));
    }
}