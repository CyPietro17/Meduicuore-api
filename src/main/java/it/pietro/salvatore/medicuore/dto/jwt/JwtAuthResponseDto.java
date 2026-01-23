package it.pietro.salvatore.medicuore.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO for JWT authentication response.
 * <p>
 * Contains:
 * - Access token for API requests
 * - Refresh token to renew the access token
 * - Authenticated user information
 * - Token metadata (expiration, type)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponseDto {

  /**
   * JWT access token to authenticate API requests.
   */
  private String accessToken;

  /**
   * JWT refresh token to obtain a new access token.
   */
  private String refreshToken;

  /**
   * Token type (always "Bearer" for JWT).
   */
  @Builder.Default
  private String tokenType = "Bearer";

  /**
   * Username of the authenticated user.
   */
  private String username;

  /**
   * User's email address.
   */
  private String email;

  /**
   * Roles assigned to the user.
   */
  private List<String> roles;

  /**
   * Expiration timestamp of the access token.
   */
  private Instant accessTokenExpiresAt;

  /**
   * Expiration timestamp of the refresh token.
   */
  private Instant refreshTokenExpiresAt;
}
