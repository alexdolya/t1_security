package ru.dolya.t1_security.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthResponseDto {

    private String accessToken;
    private String refreshToken;

}
