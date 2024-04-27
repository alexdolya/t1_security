package ru.dolya.t1_security.service;

import org.springframework.transaction.annotation.Transactional;
import ru.dolya.t1_security.model.dto.RegistrationRequestDto;
import ru.dolya.t1_security.model.entity.UserEntity;

public interface UserService {
    @Transactional
    Long registration(RegistrationRequestDto registrationRequestDto);

    @Transactional
    UserEntity findByUsername(String username);

    @Transactional
    UserEntity save(UserEntity userEntity);

    @Transactional
    UserEntity findByRefreshToken(String refreshToken);
}
