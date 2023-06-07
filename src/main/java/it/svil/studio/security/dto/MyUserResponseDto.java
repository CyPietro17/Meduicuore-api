package it.svil.studio.security.dto;

import it.svil.studio.security.model.Role;
import lombok.Data;

@Data
public class MyUserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
}
