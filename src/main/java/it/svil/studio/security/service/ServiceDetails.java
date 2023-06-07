package it.svil.studio.security.service;

import it.svil.studio.security.model.MyUser;
import it.svil.studio.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ServiceDetails implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser myUser = userRepo.findByUsername(username);
        if(username != null){
            Details user = new Details(myUser);
            return user;
        }
        return null;
    }
}
