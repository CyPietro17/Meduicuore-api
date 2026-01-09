package it.pietro.salvatore.medicuore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pietro.salvatore.medicuore.config.security.jwt.JWTService;
import it.pietro.salvatore.medicuore.dto.jwt.JwtAuthResponse;
import it.pietro.salvatore.medicuore.dto.request.LoginRequest;
import it.pietro.salvatore.medicuore.dto.request.RefreshTokenRequest;
import it.pietro.salvatore.medicuore.dto.response.ErrorResponse;
import it.pietro.salvatore.medicuore.dto.response.LogoutResponse;
import it.pietro.salvatore.medicuore.dto.response.ResponseDto;
import it.pietro.salvatore.medicuore.dto.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static it.pietro.salvatore.medicuore.utils.HttpResponseStatus.*;

/**
 * Controller per la gestione dell'autenticazione JWT.
 * <p>
 * Endpoints:
 * - POST /auth/login - Autenticazione con username/password
 * - POST /auth/refresh - Rinnovo access token tramite refresh token
 * - POST /auth/logout - Logout (client-side, invalida token)
 * - GET /auth/me - Informazioni utente autenticato
 * <p>
 * Utilizza JWT per l'autenticazione stateless.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoint per autenticazione e gestione token JWT")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  private final UserDetailsService userDetailsService;

  /**
   * Endpoint di login.
   * Autentica l'utente con username e password, e restituisce access token e refresh token.
   *
   * @param loginRequest credenziali di login (username e password)
   * @return JWT access token e refresh token con informazioni utente
   */
  @PostMapping("/login")
  @Operation(summary = "User Login",
    description = "Authenticate the user through username and password. Return access token and refresh token.")
  @ApiResponse(responseCode = "200", description = "Authentication success",
    content = @Content(schema = @Schema(implementation = JwtAuthResponse.class)))
  @ApiResponse(responseCode = "401", description = "Invalid credential",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request (missing fields)",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<ResponseDto> login(@Validated @RequestBody LoginRequest loginRequest) {
    try {
      log.info("Attempting login for user: {}", loginRequest.getUsername());

      // 1. Autentica con username e password
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      // 2. Imposta l'autenticazione nel SecurityContext
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // 3. Carica i dettagli dell'utente
      UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

      // 4. Genera access token e refresh token
      String accessToken = jwtService.generateAccessToken(userDetails);
      String refreshToken = jwtService.generateRefreshToken(userDetails);

      // 5. Costruisci la risposta
      JwtAuthResponse response = JwtAuthResponse.builder()
                                   .accessToken(accessToken)
                                   .refreshToken(refreshToken)
                                   .tokenType("Bearer")
                                   .username(userDetails.getUsername())
                                   .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                                   .accessTokenExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                                   .refreshTokenExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                                   .build();

      log.info("Login successful for user: {}", loginRequest.getUsername());
      return ResponseEntity.ok(response);

    } catch (BadCredentialsException e) {
      log.warn("Failed login attempt for user: {} - Invalid credentials", loginRequest.getUsername());
      return ResponseEntity.status(UNAUTHORIZED_STATUS)
               .body(ErrorResponse.builder()
                       .status(UNAUTHORIZED_STATUS.value())
                       .error(UNAUTHORIZED_STATUS.getReasonPhrase())
                       .message("Username o password non validi")
                       .timestamp(Instant.now())
                       .build());
    } catch (Exception e) {
      log.error("Error during login for user: {}", loginRequest.getUsername(), e);
      return ResponseEntity.internalServerError()
               .body(ErrorResponse.builder()
                       .status(INTERNAL_SERVER_ERROR_STATUS.value())
                       .error(INTERNAL_SERVER_ERROR_STATUS.getReasonPhrase())
                       .message("Errore durante l'autenticazione")
                       .timestamp(Instant.now())
                       .build());
    }
  }

  /**
   * Endpoint per rinnovare l'access token usando il refresh token.
   * Quando l'access token scade (dopo 15 min), usa questo endpoint per ottenerne uno nuovo
   * senza dover rifare il login.
   *
   * @param refreshRequest contiene il refresh token
   * @return nuovo access token (il refresh token rimane lo stesso)
   */
  @PostMapping("/refresh")
  @Operation(summary = "Rinnova access token",
    description = "Genera un nuovo access token usando il refresh token. Il refresh token rimane valido.")
  @ApiResponse(responseCode = "200", description = "Token rinnovato con successo",
    content = @Content(schema = @Schema(implementation = JwtAuthResponse.class)))
  @ApiResponse(responseCode = "401", description = "Refresh token non valido o scaduto",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Token non è un refresh token",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<ResponseDto> refreshToken(@Validated @RequestBody RefreshTokenRequest refreshRequest) {
    try {
      String refreshToken = refreshRequest.getRefreshToken();

      log.debug("Attempting to refresh token");

      // 1. Verifica che sia effettivamente un refresh token
      if (!jwtService.isRefreshToken(refreshToken)) {
        log.warn("Invalid token type - not a refresh token");
        return ResponseEntity.badRequest()
                 .body(ErrorResponse.builder()
                         .status(BAD_REQUEST_STATUS.value())
                         .error(BAD_REQUEST_STATUS.getReasonPhrase())
                         .message("Il token fornito non è un refresh token valido")
                         .timestamp(Instant.now())
                         .build());
      }

      // 2. Estrai username dal refresh token
      String username = jwtService.extractUsername(refreshToken);

      // 3. Carica i dettagli dell'utente
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // 4. Valida il refresh token
      if (!jwtService.isTokenValid(refreshToken, userDetails)) {
        log.warn("Refresh token validation failed for user: {}", username);
        return ResponseEntity.status(UNAUTHORIZED_STATUS)
                 .body(ErrorResponse.builder()
                         .status(UNAUTHORIZED_STATUS.value())
                         .error(UNAUTHORIZED_STATUS.getReasonPhrase())
                         .message("Refresh token non valido o scaduto")
                         .timestamp(Instant.now())
                         .build());
      }

      // 5. Genera un NUOVO access token
      String newAccessToken = jwtService.generateAccessToken(userDetails);

      // 6. Costruisci la risposta (refresh token rimane lo stesso)
      JwtAuthResponse response =
        JwtAuthResponse.builder()
          .accessToken(newAccessToken)
          .refreshToken(refreshToken)  // Stesso refresh token
          .tokenType("Bearer")
          .username(userDetails.getUsername())
          .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
          .accessTokenExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
          .refreshTokenExpiresAt(jwtService.extractExpiration(refreshToken).toInstant())
          .build();

      log.info("Token refreshed successfully for user: {}", username);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error refreshing token", e);
      return ResponseEntity.status(UNAUTHORIZED_STATUS)
               .body(ErrorResponse.builder()
                       .status(UNAUTHORIZED_STATUS.value())
                       .error(UNAUTHORIZED_STATUS.getReasonPhrase())
                       .message("Errore durante il rinnovo del token")
                       .timestamp(Instant.now())
                       .build());
    }
  }

  /**
   * Endpoint per il logout.
   * Nota: Con JWT stateless, il logout è gestito principalmente lato client
   * (rimuovendo i token dal localStorage/sessionStorage).
   * <p>
   * Questo endpoint serve per:
   * - Logging dell'evento di logout
   * - Eventuale blacklist del token (implementazione futura)
   * - Clear del SecurityContext
   *
   * @return conferma di logout
   */
  @PostMapping("/logout")
  @Operation(summary = "Logout utente",
    description = "Effettua il logout. Il client deve rimuovere i token JWT salvati. Il server pulisce il SecurityContext.")
  @ApiResponse(responseCode = "200", description = "Logout effettuato con successo")
  public ResponseEntity<ResponseDto> logout() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      String username = authentication.getName();
      log.info("User {} logged out", username);

      // Clear SecurityContext
      SecurityContextHolder.clearContext();

      return ResponseEntity.ok().body(new LogoutResponse("Logout effettuato con successo"));
    }

    return ResponseEntity.ok().body(new LogoutResponse("Nessuna sessione attiva"));
  }

  /**
   * Endpoint per ottenere informazioni sull'utente autenticato.
   * Richiede un access token valido.
   *
   * @return informazioni sull'utente corrente
   */
  @GetMapping("/me")
  @Operation(summary = "Informazioni utente corrente",
    description = "Restituisce le informazioni dell'utente autenticato tramite il token JWT.")
  @ApiResponse(responseCode = "200", description = "Informazioni utente recuperate con successo",
    content = @Content(schema = @Schema(implementation = UserInfoResponse.class)))
  @ApiResponse(responseCode = "401", description = "Non autenticato - token mancante o non valido",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<ResponseDto> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(UNAUTHORIZED_STATUS)
               .body(ErrorResponse.builder()
                       .status(UNAUTHORIZED_STATUS.value())
                       .error(UNAUTHORIZED_STATUS.getReasonPhrase())
                       .message("Utente non autenticato")
                       .timestamp(Instant.now())
                       .build());
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    assert userDetails != null;
    UserInfoResponse response = UserInfoResponse.builder()
                                  .username(userDetails.getUsername())
                                  .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                                  .authenticated(true)
                                  .build();

    return ResponseEntity.ok(response);
  }
}