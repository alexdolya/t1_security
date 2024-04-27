package ru.dolya.t1_security.model.dto;

import lombok.Data;
import ru.dolya.t1_security.model.RoleType;

import java.util.List;

@Data
public class RegistrationRequestDto {

    private String username;
    private String password;
    private List<RoleType> roles;
}
