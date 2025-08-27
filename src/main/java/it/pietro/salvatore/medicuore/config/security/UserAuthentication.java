//package it.pietro.salvatore.medicuore.config.security;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//
//import javax.security.auth.Subject;
//import java.util.Collection;
//import java.util.List;
//
//public class UserAuthentication implements Authentication {
//
//  @Override
//  public Collection<? extends GrantedAuthority> getAuthorities() {
//    return List.of();
//  }
//
//  @Override
//  public Object getCredentials() {
//    return null;
//  }
//
//  @Override
//  public Object getDetails() {
//    return null;
//  }
//
//  @Override
//  public Object getPrincipal() {
//    return null;
//  }
//
//  @Override
//  public boolean isAuthenticated() {
//    return false;
//  }
//
//  @Override
//  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
//
//  }
//
//  @Override
//  public String getName() {
//    return "";
//  }
//
//  @Override
//  public boolean implies(Subject subject) {
//    return Authentication.super.implies(subject);
//  }
//}
