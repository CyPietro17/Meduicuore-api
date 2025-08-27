package it.pietro.salvatore.medicuore.utils.dtocast;

import it.pietro.salvatore.medicuore.dto.response.MyUserResponseDto;
import it.pietro.salvatore.medicuore.entity.MyUser;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserCast {

  private static PasswordEncoder passwordEncoder;

  public static MyUserResponseDto castUser(MyUser myUser) {
    MyUserResponseDto responseDto = new MyUserResponseDto();
    responseDto.setId(myUser.getId());
    responseDto.setUsername(myUser.getUsername());
    responseDto.setEmail(myUser.getEmail());
    responseDto.setPassword(myUser.getPassword());
    responseDto.setRole(myUser.getRole());
    return responseDto;
  }
}
