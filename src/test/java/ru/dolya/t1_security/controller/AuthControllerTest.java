package ru.dolya.t1_security.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.dolya.t1_security.model.dto.AuthRequestDto;
import ru.dolya.t1_security.model.dto.AuthResponseDto;
import ru.dolya.t1_security.model.dto.RegistrationRequestDto;
import ru.dolya.t1_security.secutiry.UserPrincipal;
import ru.dolya.t1_security.service.impl.AuthServiceImpl;
import ru.dolya.t1_security.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    UserServiceImpl userService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    AuthServiceImpl authService;

    @InjectMocks
    AuthController authController;

    @Test
    void registration() {
        doReturn(1L)
                .when(userService).registration(any());
        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();

        Long id = authController.registration(registrationRequestDto);

        assertEquals(1L, id);
        verify(userService, times(1)).registration(registrationRequestDto);
    }

    @Test
    void auth() {
        String username = "testUser";
        String password = "testPassword";
        AuthRequestDto authRequestDto = new AuthRequestDto()
                .setUsername(username)
                .setPassword(password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(1L)
                .username(username)
                .password(password)
                .authorities(Collections.emptyList())
                .build();
        Authentication authentication = new TestingAuthenticationToken(userPrincipal, password);
        doReturn(authentication)
                .when(authenticationManager).authenticate(authenticationToken);
        doReturn(new AuthResponseDto())
                .when(authService).auth(userPrincipal);

        AuthResponseDto authResponseDto = authController.auth(authRequestDto);

        assertNotNull(authResponseDto);
        verify(authenticationManager, times(1)).authenticate(authenticationToken);
        verify(authService, times(1)).auth(userPrincipal);
    }

    @Test
    void refreshToken() {
        String refreshToken = UUID.randomUUID().toString();

        authController.refreshToken(refreshToken);

        verify(authService, times(1)).refresh(refreshToken);
    }

}