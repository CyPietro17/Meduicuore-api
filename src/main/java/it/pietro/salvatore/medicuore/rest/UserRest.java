package it.pietro.salvatore.medicuore.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pietro.salvatore.medicuore.config.security.auth.AuthenticationRequest;
import it.pietro.salvatore.medicuore.config.security.auth.AuthenticationResponse;
import it.pietro.salvatore.medicuore.dto.request.MyUserRequestDto;
import it.pietro.salvatore.medicuore.dto.response.MyUserResponseDto;
import it.pietro.salvatore.medicuore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "Users", description = "Sign in - Authentication - Authorization")
public class UserRest {

  private static final Logger LOG = Logger.getLogger(UserRest.class.getName());

  private final UserService userService;

  @PutMapping(value = "/register")
  @Operation(description = "User Registration")
  public ResponseEntity<MyUserResponseDto> add(@RequestBody MyUserRequestDto requestDto) {
    ResponseEntity<MyUserResponseDto> httpResponse;
    MyUserResponseDto responseDto = userService.response(userService.addUser(requestDto));
    if (responseDto.getId() != -1L) {
      httpResponse = ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    } else {
      httpResponse = ResponseEntity.badRequest().body(responseDto);
    }
    return httpResponse;
  }

  @PostMapping(value = "/login")
  @Operation(description = "User authentication")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest credentials) {
    ResponseEntity<AuthenticationResponse> httpResponse;
    AuthenticationResponse authenticationResponse = userService.login(credentials);
    if (authenticationResponse != null) {
      httpResponse = ResponseEntity.ok(authenticationResponse);
    } else {
      httpResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    return httpResponse;
  }

//  @PostMapping(value = "/login")
//  @Operation(description = "User authentication")
//  public ResponseEntity<AuthenticationResponse> login(@RequestBody MyUserRequestDto requestDto) {
//    ResponseEntity<AuthenticationResponse> httpResponse;
//    MyUserResponseDto responseDto = userService.response(userService.findUser(requestDto));
//    if (responseDto.getId() != -1L) {
//      httpResponse = ResponseEntity.ok(AuthenticationResponse.builder()
//                                         .token(userService.login(requestDto).getToken())
//                                         .username(responseDto.getUsername())
//                                         .roles(responseDto.getRole().name())
//                                         .build());
//    } else {
//      LOG.warning("User " + requestDto.getUsername() + " not found");
//      httpResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//    }
//    return httpResponse;
//  }

//  @PostMapping(value = "/login-jwt")
//  @Operation(description = "User authentication")
//  public ResponseEntity<String> loginjwt(@RequestBody MyUserRequestDto requestDto) {
//    ResponseEntity<String> httpResponse;
//    AuthenticationResponse response = userService.login(requestDto);
//    if (response.getToken() != null) {
//      httpResponse = ResponseEntity.ok(response.getToken());
//    } else {
//      httpResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
//    }
//    return httpResponse;
//  }

  @RequestMapping(value = "/auth")
  @Operation(description = "Response 200 ad avvenuta autenticazione")
  public ResponseEntity<MyUserResponseDto> userAuthenticated(MyUserRequestDto authRequest) {
    ResponseEntity<MyUserResponseDto> httpResponse;
    MyUserResponseDto user = userService.response(userService.userByUsername(authRequest.getUsername()));
    if (user != null) {
      httpResponse = ResponseEntity.ok(user);
    } else {
      httpResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    return httpResponse;
  }

//  @PostMapping(value = "/role")
//  @Operation(description = "Return the user's role if is logged")
//  public ResponseEntity<String> roleUser(@RequestBody MyUserRequestDto requestDto) {
//    ResponseEntity<String> httpResponse;
//    try {
//      MyUserResponseDto responseDto = userService.response(userService.findUser(requestDto));
//      httpResponse = ResponseEntity.ok(responseDto.getRole().name());
//    } catch (Exception e) {
//      LOG.warning("User " + requestDto.getUsername() + " not found");
//      httpResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//    }
//    return httpResponse;
//  }
}
