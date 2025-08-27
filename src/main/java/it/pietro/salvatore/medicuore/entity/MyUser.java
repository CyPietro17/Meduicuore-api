package it.pietro.salvatore.medicuore.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serial;
import java.io.Serializable;

@Data
@Alias(value = "user")
public class MyUser implements Serializable {

  @Serial
  private static final long serial = 1L;

  private Long id;
  private String username;
  private String email;
  private String password;
  //    @Enumerated(EnumType.STRING)
  private Role role;
}
