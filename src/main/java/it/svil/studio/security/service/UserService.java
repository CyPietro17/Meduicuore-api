package it.svil.studio.security.service;

import it.svil.studio.security.dto.MyUserRequestDto;
import it.svil.studio.security.dto.MyUserResponseDto;
import it.svil.studio.security.model.MyUser;
import it.svil.studio.security.model.Role;
import it.svil.studio.security.repo.UserRepo;
import it.svil.studio.util.UserCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MyUser addUser(MyUserRequestDto requestDto){
        MyUser myUser = new MyUser();
        myUser.setUsername(requestDto.getUsername());
        myUser.setEmail(requestDto.getEmail());
        myUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        if(this.userList().isEmpty())
            myUser.setRole(Role.ADMIN);
        else if(userRepository.findByUsername(requestDto.getUsername()) == null)
            myUser.setRole(Role.USER);
        else return null;

        return userRepository.save(myUser);
    }

    public List<MyUser> userList(){
        return userRepository.findAll();
    }

    public MyUser findUser(MyUserRequestDto requestDto){
        MyUser user = userRepository.findByUsername(requestDto.getUsername());
        if(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())){
            return user;
        }

        System.out.println("Service: Utente " + requestDto.getUsername() + " non trovato");
        return null;
    }

    public MyUserResponseDto response(MyUser myUser){
        return UserCast.castUser(myUser);
    }
}
