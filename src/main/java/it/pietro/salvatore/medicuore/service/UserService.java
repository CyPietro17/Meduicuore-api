package it.pietro.salvatore.medicuore.service;

import it.pietro.salvatore.medicuore.config.security.auth.AuthenticationRequest;
import it.pietro.salvatore.medicuore.config.security.auth.AuthenticationResponse;
import it.pietro.salvatore.medicuore.config.security.jwt.JWTService;
import it.pietro.salvatore.medicuore.dto.request.MyUserRequestDto;
import it.pietro.salvatore.medicuore.dto.response.MyUserResponseDto;
import it.pietro.salvatore.medicuore.entity.MyUser;
import it.pietro.salvatore.medicuore.entity.Role;
import it.pietro.salvatore.medicuore.mappers.UserMapper;
import it.pietro.salvatore.medicuore.utils.dtocast.UserCast;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserService {

  private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JWTService jwtService;

  public MyUser addUser(MyUserRequestDto requestDto) {
    MyUser myUser = new MyUser();
    if (userMapper.findByUsername(requestDto.getUsername()) == null) {
      myUser.setUsername(requestDto.getUsername());
      myUser.setEmail(requestDto.getEmail());
      myUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
      if (this.userList().isEmpty()) {
        myUser.setRole(Role.ADMIN);
      } else {
        myUser.setRole(Role.USER);
      }
      userMapper.save(myUser);
    } else {
      LOGGER.warning(requestDto.getUsername() + " already exists");
      myUser.setId(-1L);
    }
    return myUser;
  }

  public List<MyUser> userList() {
    return userMapper.findAll();
  }

  public AuthenticationResponse login(AuthenticationRequest credentials) {
    MyUser user = userMapper.findByUsername(credentials.getUsername());
    AuthenticationResponse authenticationResponse = null;
    if (user != null && passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
      authenticationResponse = AuthenticationResponse.builder().token(jwtService.generateToken(credentials.getUsername())).username(
        credentials.getUsername()).roles(user.getRole().name()).build();
    }
    return authenticationResponse;
  }

  public MyUser findUser(AuthenticationRequest credentials) {
    MyUser user = userMapper.findByUsername(credentials.getUsername());
    if (user == null) {
      user = new MyUser();
      user.setId(-1L);
      LOGGER.warning("Service: User " + credentials.getUsername() + " not found");
    } else if (!passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
      LOGGER.warning("Service: Password of " + credentials.getUsername() + " not matching");
    }

    return user;
  }

  public MyUser userByUsername(String username) {
    return userMapper.findByUsername(username);
  }

  //  public AuthenticationResponse authentication(AuthenticationRequest request) {
  //
  //  }

  public MyUserResponseDto response(MyUser myUser) {
    return UserCast.castUser(myUser);
  }
}
