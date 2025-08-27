//package it.pietro.salvatore.medicuore.config.security;
//
//import it.pietro.salvatore.medicuore.entity.MyUser;
//import it.pietro.salvatore.medicuore.mappers.UserMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class UserAuthenticationProvider implements AuthenticationProvider {
//
//  @Autowired
//  private UserMapper userMapper;
//  @Autowired
//  private PasswordEncoder passwordEncoder;
//
//  @Override
//  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//    String username = authentication.getName();
//    String password = authentication.getCredentials().toString();
//    MyUser user = userMapper.findByUsername(username);
//    if (user != null) {
//      if (passwordEncoder.matches(password, user.getPassword())) {
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
//        return new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
//      } else {
//        throw new BadCredentialsException("Invalid password");
//      }
//    } else {
//      throw new BadCredentialsException("Bad credentials");
//    }
//  }
//
//  @Override
//  public boolean supports(Class<?> authentication) {
//    return (UserAuthenticationProvider.class.isAssignableFrom(authentication));
//  }
//}
