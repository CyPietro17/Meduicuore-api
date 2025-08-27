package it.pietro.salvatore.medicuore.config.security;

import it.pietro.salvatore.medicuore.config.security.auth.user.ServiceDetails;
import it.pietro.salvatore.medicuore.config.security.csrf.CsrfCookieFilter;
import it.pietro.salvatore.medicuore.config.security.csrf.SpaCsrfTokenRequestHandler;
import it.pietro.salvatore.medicuore.config.security.jwt.JWTTokenGeneratorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SecurityConfig {

  private static final String[] AUTH_LIST = {"/swagger-ui/**",
    "/swagger-ui.html",
    "/medicuore/swagger-ui/",
    "/medicuore-swagger-ui.html",
    "/v2/api-docs/**",
    "/v3/api-docs/**",
    "/swagger-resources/**",
    "/webjars/**",
    "/register",
    "/login",
    "/auth",
    "/role"};

  private final ServiceDetails customDetailsService;
  private final JWTTokenGeneratorFilter jwtFilter;

  /**
   * Encoding Password of user
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(customDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      //    http.securityContext(secConf -> secConf.requireExplicitSave(false))
      //      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
      .cors(corsSetting -> corsSetting.configurationSource(corsConfigurationFilter()))
      .csrf(AbstractHttpConfigurer::disable)
//      .csrf(this::httpCsrfConfiguration)
//      .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
      .authorizeHttpRequests(auth -> auth.requestMatchers(AUTH_LIST)
                                       .permitAll()
                                       .requestMatchers("/reparti/nuovo", "/impiegati/aggiungi", "/impiegati/dimissione")
                                       .hasAuthority("ADMIN")
                                       .requestMatchers("/reparti/**", "/impiegati/**", "pazienti/**", "ricoveri/**")
                                       .authenticated()
                                       .anyRequest()
                                       .authenticated())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
      .authenticationProvider(authenticationProvider())
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .httpBasic(withDefaults());

    return http.build();
  }

  private void httpCsrfConfiguration(CsrfConfigurer<HttpSecurity> csrf) {
    csrf.ignoringRequestMatchers("/register", "/login")
      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());
  }

  private CorsConfigurationSource corsConfigurationFilter() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedMethods(
      List.of(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name(),
        HttpMethod.OPTIONS.name()));
    corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
    corsConfiguration.setAllowedHeaders(
      List.of(HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION, HttpHeaders.ORIGIN, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
        HttpHeaders.ACCEPT, HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
        HttpHeaders.COOKIE, HttpHeaders.SET_COOKIE, "X-Requested-With", "X-XSRF-TOKEN", "XSRF-TOKEN"));
    corsConfiguration.setExposedHeaders(
      List.of(HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION,
        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, HttpHeaders.COOKIE,
        HttpHeaders.SET_COOKIE));
    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    return urlBasedCorsConfigurationSource;
  }
}