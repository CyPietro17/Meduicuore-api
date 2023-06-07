package it.svil.studio.security.dto;

import lombok.Data;

@Data
public class MyUserRequestDto {

    private String username;
    private String email;
    private String password;
}
