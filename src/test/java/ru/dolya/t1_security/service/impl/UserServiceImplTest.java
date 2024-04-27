package ru.dolya.t1_security.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.dolya.t1_security.exception.UserAlreadyExistException;
import ru.dolya.t1_security.model.dto.RegistrationRequestDto;
import ru.dolya.t1_security.model.entity.UserEntity;
import ru.dolya.t1_security.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void registration() {
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("testPassword");

        when(userRepository.existsByUsername("testUser")).thenReturn(false);

        UserEntity savedUser = UserEntity.builder()
                .userId(1L)
                .username("testUser")
                .password("encodedPassword")
                .build();

        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(savedUser);

        Long userId = userService.registration(requestDto);

        assertEquals(1L, userId);
    }

    @Test
    void registrationThrowException() {
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setUsername("existingUser");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.registration(requestDto));
    }

    @Test
    void findByUsername() {
        UserEntity userEntity = UserEntity.builder()
                .username("testUser")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userEntity));

        UserEntity result = userService.findByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void findByUsernameThrowException() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername("nonExistingUser"));
    }

    @Test
    void save() {
        UserEntity userEntity = UserEntity.builder()
                .username("testUser")
                .password("encodedPassword")
                .build();

        when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserEntity result = userService.save(userEntity);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void findByRefreshToken() {
        UserEntity userEntity = UserEntity.builder()
                .username("testUser")
                .password("encodedPassword")
                .refreshToken("testToken")
                .build();

        when(userRepository.findByRefreshToken("testToken")).thenReturn(Optional.of(userEntity));

        UserEntity result = userService.findByRefreshToken("testToken");

        assertNotNull(result);
        assertEquals("testToken", result.getRefreshToken());
    }

    @Test
    void findByRefreshTokenThrowException() {
        when(userRepository.findByRefreshToken("nonExistingToken")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findByRefreshToken("nonExistingToken"));
    }
}