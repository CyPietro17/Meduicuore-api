package it.pietro.salvatore.medicuore.dto.response;

import it.pietro.salvatore.medicuore.entity.Role;
import lombok.Data;

@Data
public class MyUserResponseDto {

  private Long id;
  private String username;
  private String email;
  private String password;
  private Role role;
}
