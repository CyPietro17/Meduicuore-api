package it.pietro.salvatore.medicuore.dto.request;

import lombok.Data;

@Data
public class MyUserRequestDto {

  private String username;
  private String email;
  private String password;
}
