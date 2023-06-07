package it.svil.studio.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.svil.studio.security.Authenticaton;
import it.svil.studio.security.dto.MyUserRequestDto;
import it.svil.studio.security.dto.MyUserResponseDto;
import it.svil.studio.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
//@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Utenti", description = "Registrazione - Autenticazione - Autorizzazione API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping(value = "/register")
    @Operation(description = "Registrazione nuovo utente")
    public ResponseEntity<MyUserResponseDto> add(@RequestBody MyUserRequestDto requestDto){
        MyUserResponseDto responseDto = userService.response(userService.addUser(requestDto));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    @Operation(description = "Autenticazione utente")
    public ResponseEntity<MyUserResponseDto> findUtente(@RequestBody MyUserRequestDto requestDto){
        try {
            MyUserResponseDto responseDto = userService.response(userService.findUser(requestDto));
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception e){
            System.out.println("Utente " + requestDto.getUsername() + " non trovato");
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/auth")
    @Operation(description = "Response 200 ad avvenuta autenticazione")
    public ResponseEntity<Authenticaton> utserLoggato(){
        return new ResponseEntity<>(new Authenticaton("Authentication ok"), HttpStatus.OK);
    }

    @PostMapping(value = "/role")
    @Operation(description = "Ritorna il ruolo dell'utente se esso è loggato")
    public ResponseEntity<String> roleUser(@RequestBody MyUserRequestDto requestDto){
        try{
            MyUserResponseDto responseDto = userService.response(userService.findUser(requestDto));
            return new ResponseEntity<>(responseDto.getRole().name(), HttpStatus.OK);
        } catch (Exception e){
            System.out.println("Utente " + requestDto.getUsername() + " non trovato");
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
