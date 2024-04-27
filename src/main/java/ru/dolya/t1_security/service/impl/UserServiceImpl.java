package ru.dolya.t1_security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dolya.t1_security.exception.UserAlreadyExistException;
import ru.dolya.t1_security.model.dto.RegistrationRequestDto;
import ru.dolya.t1_security.model.entity.UserEntity;
import ru.dolya.t1_security.repository.UserRepository;
import ru.dolya.t1_security.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public Long registration(RegistrationRequestDto registrationRequestDto) {
        if (userRepository.existsByUsername(registrationRequestDto.getUsername())) {
            throw new UserAlreadyExistException("Пользователь с именем "
                    + registrationRequestDto.getUsername() + " уже существует");
        }
        UserEntity userEntity = UserEntity.builder()
                .username(registrationRequestDto.getUsername())
                .password(bCryptPasswordEncoder.encode(registrationRequestDto.getPassword()))
                .roles(registrationRequestDto.getRoles())
                .build();
        return userRepository.save(userEntity).getUserId();
    }

    @Override
    @Transactional
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с именем \"" + username + "\" не найден"));
    }

    @Override
    @Transactional
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public UserEntity findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с токеном \"" + refreshToken + "\" не найден"));
    }

}
