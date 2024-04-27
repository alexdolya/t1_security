package ru.dolya.t1_security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.dolya.t1_security.model.dto.AuthRequestDto;
import ru.dolya.t1_security.model.dto.AuthResponseDto;
import ru.dolya.t1_security.model.dto.RegistrationRequestDto;
import ru.dolya.t1_security.secutiry.UserPrincipal;
import ru.dolya.t1_security.service.AuthService;
import ru.dolya.t1_security.service.UserService;

@RestController
@RequiredArgsConstructor
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/registration")
    public Long registration(@RequestBody RegistrationRequestDto registrationRequestDto) {
        return userService.registration(registrationRequestDto);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/auth")
    public AuthResponseDto auth(@RequestBody @Validated AuthRequestDto authRequestDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return authService.auth(userPrincipal);
    }

    @Operation(summary = "Получение нового access токена с помощью refresh токена")
    @PostMapping("/refresh")
    public AuthResponseDto refreshToken(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @Operation(summary = "Запрос с доступом для аутентифицированных пользователей")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/secured")
    public String helloWorld() {
        return "Hello, from secured!";
    }

    @Operation(summary = "Запрос с доступом только для администратора")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admin")
    public String helloAdmin() {
        return "Hello, ADMIN!";
    }

    @Operation(summary = "Запрос с доступом только для пользователя")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user")
    public String helloUser() {
        return "Hello, USER!";
    }
}
