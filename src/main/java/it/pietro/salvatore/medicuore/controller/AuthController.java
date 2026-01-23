package it.pietro.salvatore.medicuore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pietro.salvatore.medicuore.config.security.jwt.JWTService;
import it.pietro.salvatore.medicuore.dto.jwt.JwtAuthResponseDto;
import it.pietro.salvatore.medicuore.dto.request.LoginRequestDto;
import it.pietro.salvatore.medicuore.dto.request.RefreshTokenRequestDto;
import it.pietro.salvatore.medicuore.dto.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Controller for JWT authentication management.
 * <p>
 * Endpoints:
 * - POST /auth/login - Authentication with username/password
 * - POST /auth/refresh - Access token renewal via refresh token
 * - POST /auth/logout - Logout (client-side, invalidates token)
 * - GET /auth/me - Authenticated user information
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authentication and JWT token management")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  private final UserDetailsService userDetailsService;

  /**
   * Login endpoint.
   * Authenticates the user with username and password, and returns access token and refresh token.
   *
   * @param loginRequestDto login credentials (username and password)
   * @return JWT access token and refresh token with user information
   */
  @PostMapping("/login")
  @Operation(summary = "User Login",
    description = "Authenticate the user through username and password. Return access token and refresh token.")
  @ApiResponse(responseCode = "200", description = "Authentication success",
    content = @Content(schema = @Schema(implementation = JwtAuthResponseDto.class)))
  @ApiResponse(responseCode = "401", description = "Invalid credential",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request (missing fields)",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<JwtAuthResponseDto> login(@Validated @RequestBody LoginRequestDto loginRequestDto) {
    log.info("Attempting login for user: {}", loginRequestDto.getUsername());

    // 1. Authenticate with username and password
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));

    // 2. Set authentication in SecurityContext
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 3. Load user details
    UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.getUsername());

    // 4. Generate access token and refresh token
    String accessToken = jwtService.generateAccessToken(userDetails);
    String refreshToken = jwtService.generateRefreshToken(userDetails);

    // 5. Build response
    JwtAuthResponseDto response = JwtAuthResponseDto.builder()
                                    .accessToken(accessToken)
                                    .refreshToken(refreshToken)
                                    .tokenType("Bearer")
                                    .username(userDetails.getUsername())
                                    .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                                    .accessTokenExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                                    .refreshTokenExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                                    .build();

    log.info("Login successful for user: {}", loginRequestDto.getUsername());
    return ResponseEntity.ok(response);

  }

  /**
   * Endpoint to renew the access token using the refresh token.
   * When the access token expires (after 15 min), use this endpoint to get a new one
   * without having to log in again.
   *
   * @param refreshRequest contains the refresh token
   * @return new access token (the refresh token remains the same)
   */
  @PostMapping("/refresh")
  @Operation(summary = "Renew access token",
    description = "Generates a new access token using the refresh token. The refresh token remains valid.")
  @ApiResponse(responseCode = "200", description = "Token renewed successfully",
    content = @Content(schema = @Schema(implementation = JwtAuthResponseDto.class)))
  @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Token is not a refresh token",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<JwtAuthResponseDto> refreshToken(@Validated @RequestBody RefreshTokenRequestDto refreshRequest) {
    String refreshToken = refreshRequest.getRefreshToken();

    log.debug("Attempting to refresh token");

    // 1. Verify that it is actually a refresh token
    if (!jwtService.isRefreshToken(refreshToken)) {
      final String errorMessage = "Invalid token type - not a refresh token";
      log.warn(errorMessage);
      throw new InternalAuthenticationServiceException(errorMessage);
    }

    // 2. Extract username from refresh token
    String username = jwtService.extractUsername(refreshToken);

    // 3. Load user details
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // 4. Validate refresh token
    if (!jwtService.isTokenValid(refreshToken, userDetails)) {
      final String errorMessage = "Refresh token validation failed for user: " + username;
      log.warn(errorMessage);
      throw new CredentialsExpiredException(errorMessage);
    }

    // 5. Generate a NEW access token
    String newAccessToken = jwtService.generateAccessToken(userDetails);

    // 6. Build response (refresh token remains the same)
    JwtAuthResponseDto response =
      JwtAuthResponseDto.builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken)  // Same refresh token
        .tokenType("Bearer")
        .username(userDetails.getUsername())
        .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
        .accessTokenExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
        .refreshTokenExpiresAt(jwtService.extractExpiration(refreshToken).toInstant())
        .build();

    log.info("Token refreshed successfully for user: {}", username);
    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint for logout.
   * Note: With stateless JWT, logout is primarily handled client-side.
   * <p>
   * This endpoint serves for:
   * - Logging the logout event
   * - Potential token blacklisting (future implementation)
   * - Clearing the SecurityContext
   *
   * @return logout confirmation message
   */
  @PostMapping("/logout")
  @Operation(summary = "User Logout",
    description = "Performs logout. The client must remove saved JWT tokens. The server clears the SecurityContext.")
  @ApiResponse(responseCode = "200", description = "Logout successful")
  public ResponseEntity<String> logout() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      String username = authentication.getName();
      log.info("User {} logged out", username);

      // Clear SecurityContext
      SecurityContextHolder.clearContext();

      return ResponseEntity.ok().body("Logout successful");
    }

    return ResponseEntity.ok().body("No active session");
  }
}