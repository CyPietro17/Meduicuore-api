package it.pietro.salvatore.medicuore.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT management:
 * - Generation of access tokens and refresh tokens
 * - Token validation and parsing
 * - Extraction of information (claims) from tokens
 * - Verification of token expiration and validity
 */
@Slf4j
@Service
public class JWTService {

  public static final String TOKEN_TYPE = "token_type";
  public static final String ROLES = "roles";

  // JWT configuration externalized in application.yaml
  @Value("${jwt.secret}")
  private String jwtSecret;
  @Value("${jwt.access-token.expiration}")
  private long accessTokenExpirationMinutes;
  @Value("${jwt.refresh-token.expiration}")
  private long refreshTokenExpirationDays;
  @Value("${jwt.issuer}")
  private String tokenIssuer;

  // ========================================
  // TOKEN GENERATION
  // ========================================

  /**
   * Generates an access token for the authenticated user.
   * Includes username, roles, email, and other custom information.
   *
   * @param userDetails details of the authenticated user
   * @return JWT access token
   */
  public String generateAccessToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    // Add user's roles
    List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    claims.put(ROLES, roles);

    // Add token type
    claims.put(TOKEN_TYPE, "access");

    log.debug("Generating access token for user: {}", userDetails.getUsername());

    return createToken(claims, userDetails.getUsername(), accessTokenExpirationMinutes, ChronoUnit.MINUTES);
  }

  /**
   * Generates a refresh token for the user.
   * Has a longer duration and contains less information for security.
   *
   * @param userDetails user details
   * @return JWT refresh token
   */
  public String generateRefreshToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(TOKEN_TYPE, "refresh");

    log.debug("Generating refresh token for user: {}", userDetails.getUsername());

    return createToken(claims, userDetails.getUsername(), refreshTokenExpirationDays, ChronoUnit.DAYS);
  }

  /**
   * Creates a JWT token with the specified parameters.
   *
   * @param claims     additional information to include in the token
   * @param subject    user identifier (username)
   * @param expiration expiration time
   * @param unit       time unit for expiration
   * @return signed JWT token
   */
  private String createToken(Map<String, Object> claims, String subject, long expiration, ChronoUnit unit) {
    Instant now = Instant.now();
    Instant expirationTime = now.plus(expiration, unit);

    return Jwts.builder()
             .claims(claims)
             .subject(subject)
             .issuer(tokenIssuer)
             .issuedAt(Date.from(now))
             .expiration(Date.from(expirationTime))
             .signWith(getSigningKey())
             .compact();
  }

  // ========================================
  // TOKEN VALIDATION
  // ========================================

  /**
   * Validates a JWT token.
   * Verifies:
   * - Valid signature
   * - Not expired
   * - Username matches
   *
   * @param token       JWT to validate
   * @param userDetails user details to compare against
   * @return true if the token is valid, false otherwise
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      final String username = extractUsername(token);
      boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

      if (isValid) {
        log.debug("Token validation successful for user: {}", username);
      } else {
        log.warn("Token validation failed for user: {}", username);
      }

      return isValid;
    } catch (Exception e) {
      log.error("Token validation error: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Checks if a token is expired.
   *
   * @param token JWT to check
   * @return true if expired, false otherwise
   */
  public boolean isTokenExpired(String token) {
    try {
      Date expiration = extractExpiration(token);
      boolean expired = expiration.before(new Date());

      if (expired) {
        log.debug("Token expired at: {}", expiration);
      }

      return expired;
    } catch (ExpiredJwtException e) {
      log.debug("Token already expired");
      return true;
    }
  }

  /**
   * Checks if a token is a refresh token.
   *
   * @param token JWT to check
   * @return true if it is a refresh token
   */
  public boolean isRefreshToken(String token) {
    try {
      String tokenType = extractClaim(token, claims -> claims.get(TOKEN_TYPE, String.class));
      return "refresh".equals(tokenType);
    } catch (Exception e) {
      log.error("Error checking token type: {}", e.getMessage());
      return false;
    }
  }

  // ========================================
  // EXTRACT INFORMATION FROM TOKEN
  // ========================================

  /**
   * Extracts the username from the token.
   *
   * @param token JWT
   * @return user's username
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts the expiration date from the token.
   *
   * @param token JWT
   * @return expiration date
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts user roles from the token.
   *
   * @param token JWT
   * @return list of roles
   */
  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String token) {
    return extractClaim(token, claims -> (List<String>) claims.get(ROLES));
  }

  /**
   * Extracts a specific claim from the token.
   *
   * @param token          JWT
   * @param claimsResolver function to extract the desired claim
   * @param <T>            type of the claim
   * @return claim value
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts all claims from the token.
   * Handles JWT exceptions.
   *
   * @param token JWT
   * @return all claims
   * @throws JwtValidationException if the token is invalid
   */
  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      log.error("JWT token expired: {}", e.getMessage());
      throw new JwtValidationException("Token expired", e);
    } catch (UnsupportedJwtException e) {
      log.error("JWT token unsupported: {}", e.getMessage());
      throw new JwtValidationException("Token unsupported", e);
    } catch (MalformedJwtException e) {
      log.error("JWT token malformed: {}", e.getMessage());
      throw new JwtValidationException("Token malformed", e);
    } catch (SignatureException e) {
      log.error("JWT signature validation failed: {}", e.getMessage());
      throw new JwtValidationException("Invalid signature", e);
    } catch (IllegalArgumentException e) {
      log.error("JWT token compact of handler are invalid: {}", e.getMessage());
      throw new JwtValidationException("Invalid token", e);
    }
  }

  // ========================================
  // UTILITY
  // ========================================

  /**
   * Gets the signing key for tokens.
   *
   * @return secret key for signing tokens
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Custom exception for JWT validation errors.
   */
  public static class JwtValidationException extends RuntimeException {
    public JwtValidationException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}