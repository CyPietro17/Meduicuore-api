package it.pietro.salvatore.medicuore.config.security.auth.user;

import it.pietro.salvatore.medicuore.entity.MyUser;
import it.pietro.salvatore.medicuore.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ServiceDetails implements UserDetailsService {

  private final UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    MyUser myUser = userMapper.findByUsername(username);
    if (myUser != null) {
      return new Details(myUser);
    }
    return null;
  }
}
