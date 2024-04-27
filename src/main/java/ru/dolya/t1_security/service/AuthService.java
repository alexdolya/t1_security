package ru.dolya.t1_security.service;

import org.springframework.transaction.annotation.Transactional;
import ru.dolya.t1_security.model.dto.AuthResponseDto;
import ru.dolya.t1_security.secutiry.UserPrincipal;

public interface AuthService {

    @Transactional
    AuthResponseDto auth(UserPrincipal userPrincipal);

    @Transactional
    AuthResponseDto refresh(String refreshToken);
}
