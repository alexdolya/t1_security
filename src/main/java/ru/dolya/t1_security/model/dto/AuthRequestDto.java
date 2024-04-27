package ru.dolya.t1_security.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthRequestDto {

    private String username;
    private String password;
}
