package it.pietro.salvatore.medicuore.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter.
 * <p>
 * Purpose:
 * Intercepts every HTTP request and checks for a valid JWT token
 * in the Authorization header. If the token is valid, it authenticates the user
 * in the Spring Security SecurityContext.
 * <p>
 * How it works:
 * 1. Extracts the JWT token from the Authorization header (format: "Bearer TOKEN")
 * 2. Validates the token and extracts the username
 * 3. Loads user details from the database
 * 4. Verifies that the token corresponds to the user
 * 5. If everything is valid, sets the authentication in the SecurityContext
 * 6. The request proceeds to the controller
 * <p>
 * Note: OncePerRequestFilter guarantees that the filter is executed only once per request
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final int BEARER_PREFIX_LENGTH = 7;

  private final JWTService jwtService;
  private final UserDetailsService userDetailsService;

  /**
   * Main filter method, executed for every HTTP request.
   * <p>
   * Authentication flow:
   * 1. Extract token from header
   * 2. Validate Bearer format
   * 3. Extract username from token
   * 4. Verify that the user is not already authenticated
   * 5. Load user details
   * 6. Validate token vs user
   * 7. Set authentication in SecurityContext
   *
   * @param request     incoming HTTP request
   * @param response    HTTP response
   * @param filterChain filter chain to continue
   * @throws ServletException in case of servlet errors
   * @throws IOException      in case of I/O errors
   */
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {

    try {
      // Extract JWT token from request
      String jwt = extractJwtFromRequest(request);

      // If no token, proceed without authentication
      if (jwt == null) {
        log.trace("No JWT token found in request to: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
        return;
      }

      // Extract username from token
      String username = jwtService.extractUsername(jwt);

      // If username is present and user is not already authenticated
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        authenticateUser(request, jwt, username);
      }

    } catch (JWTService.JwtValidationException e) {
      // Invalid token: log error but let proceed
      // (the protected endpoint will reject the request)
      log.warn("JWT validation failed for request to {}: {}", request.getRequestURI(), e.getMessage());
    } catch (UsernameNotFoundException e) {
      log.warn("User not found for JWT token: {}", e.getMessage());
    } catch (Exception e) {
      // Generic error: log but do not block request
      log.error("Error processing JWT authentication: {}", e.getMessage(), e);
    }

    // Proceed with filter chain
    filterChain.doFilter(request, response);
  }

  /**
   * Authenticates the user based on the JWT token.
   *
   * @param request  HTTP request
   * @param jwt      extracted JWT token
   * @param username username extracted from token
   */
  private void authenticateUser(HttpServletRequest request, String jwt, String username) {
    try {
      // Load user details from database
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // Validate token against user details
      if (jwtService.isTokenValid(jwt, userDetails)) {

        // Create Spring Security authentication token
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
          null, // credentials (password) not needed after JWT authentication
          userDetails.getAuthorities());

        // Add request details (IP, session, etc.)
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set authentication in SecurityContext
        // From this moment the user is considered authenticated
        SecurityContextHolder.getContext().setAuthentication(authToken);

        log.debug("User '{}' authenticated successfully via JWT for request to: {}", username, request.getRequestURI());
      } else {
        log.warn("JWT token validation failed for user: {}", username);
      }

    } catch (UsernameNotFoundException e) {
      log.warn("User '{}' not found in database", username);
      throw e;
    }
  }

  /**
   * Extracts the JWT token from the Authorization header of the request.
   * <p>
   * Expected format: "Authorization: Bearer <token>"
   *
   * @param request HTTP request
   * @return JWT token (without "Bearer " prefix), or null if not present
   */
  private String extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

    // Verify that header is present and starts with "Bearer "
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      String token = bearerToken.substring(BEARER_PREFIX_LENGTH);

      // Verify that there is actually a token after "Bearer "
      if (StringUtils.hasText(token)) {
        return token;
      } else {
        log.warn("Bearer token is empty");
      }
    }

    return null;
  }

  /**
   * Determines if the filter should be applied to the request.
   * <p>
   * Override this method if you want to exclude some paths from the JWT filter
   * (e.g., for public endpoints).
   * <p>
   * Currently applies the filter to all requests.
   *
   * @param request HTTP request
   * @return true if the filter should be applied
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // Optional: you can exclude some paths from the JWT filter
    // E.g., for public endpoints
    final String path = request.getServletPath();
    return path.startsWith("/public/") || path.equals("/login") || path.equals("/register") || path.equals("/auth");

    // return false Apply filter to all requests
  }
}