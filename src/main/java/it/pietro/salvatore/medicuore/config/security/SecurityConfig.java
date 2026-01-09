package it.pietro.salvatore.medicuore.config.security;

import it.pietro.salvatore.medicuore.config.security.auth.user.ServiceDetails;
import it.pietro.salvatore.medicuore.config.security.jwt.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * - JWT-based authentication;
 * - CORS configuration for Angular frontend;
 * - Endpoint protection with role-based authorization;
 * - Password encoding.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  /**
   * Public paths that do not require authentication.
   */
  private static final String[] PUBLIC_ENDPOINTS = {
    // Springdoc OpenAPI paths
    "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**",
    // Public endpoints
    "/register", "/login", "/auth"};

  /**
   * Endpoints that require the ADMIN role.
   */
  private static final String[] ADMIN_ENDPOINTS = {"/reparti/nuovo", "/impiegati/aggiungi", "/impiegati/dimissione"};

  // CORS configuration externalized in application.yaml
  @Value("${cors.allowed-origins}")
  private String[] allowedOrigins;
  @Value("${cors.allowed-methods}")
  private String[] allowedMethods;
  @Value("${cors.max-age}")
  private Long corsMaxAge;

  private final ServiceDetails customDetailsService;
  private final JWTAuthenticationFilter jwtFilter;

  /**
   * Configured password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  /**
   * Configured authentication provider
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(customDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  /**
   * Bean for the AuthenticationManager needed for the login process.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }

  /**
   * CORS configuration to allow requests from the frontend.
   * <p>
   * Security:
   * - Limited origins (configurable via properties);
   * - Explicit HTTP methods;
   * - Specific headers instead of "*";
   * - Credentials enabled for JWT.
   *
   * @return CORS configuration.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    configuration.setAllowedMethods(Arrays.asList(allowedMethods));

    configuration.setAllowedHeaders(
      List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.ORIGIN, "X-Requested-With"));

    configuration.setExposedHeaders(
      List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
        HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));

    configuration.setMaxAge(corsMaxAge);

    // Apply configuration to all endpoints
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  /**
   * Main security filter chain.
   * <p>
   * Configuration:
   * 1. CORS: allows requests from frontend;
   * 2. CSRF: disabled because we use JWT (stateless);
   * 3. Authorization: defines which endpoints are public/protected;
   * 4. Session Management: STATELESS because we use JWT;
   * 5. JWT Filter: added before the standard authentication filter.
   *
   * @return configured security filter chain.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    http
      // CORS configuration
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))

      // Disable CSRF because we use JWT (stateless authentication)
      // JWT in the Authorization header is immune to CSRF attack
      .csrf(AbstractHttpConfigurer::disable)

      // Authorization configuration
      .authorizeHttpRequests(auth -> auth
                                       // Public endpoints
                                       .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                       // Only ADMIN role endpoints
                                       .requestMatchers(ADMIN_ENDPOINTS).hasAuthority("ADMIN")
                                       // Authenticated endpoints (any role)
                                       .anyRequest().authenticated())

      // Session Management: STATELESS because we use JWT
      // The server does not maintain sessions, everything is in the token
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

      // Register the custom authentication provider
      .authenticationProvider(authenticationProvider())

      // Add the JWT filter before the standard authentication filter
      // This extracts and validates the JWT token from every request
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

      // Keep HTTP Basic for backward compatibility or testing
      .httpBasic(withDefaults());

    return http.build();
  }
}