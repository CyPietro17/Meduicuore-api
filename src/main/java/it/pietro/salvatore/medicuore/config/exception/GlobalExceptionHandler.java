package it.pietro.salvatore.medicuore.config.exception;

import it.pietro.salvatore.medicuore.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Intercepts all unhandled exceptions in controllers.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 400 Bad Request
   * Handles parameter validation errors (e.g. @NotBlank, @Size).
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, List<String>> errors = ex.getBindingResult()
                                         .getFieldErrors()
                                         .stream()
                                         .collect(Collectors.groupingBy(FieldError::getField, Collectors.mapping(
                                           fe -> Objects.nonNull(fe.getDefaultMessage()) ? fe.getDefaultMessage() :
                                                   "Validation error!", Collectors.toList())));

    log.warn("Validation error on {}: {}", request.getDescription(false), errors);

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(HttpStatus.BAD_REQUEST.value())
                                    .error("Validation Failed")
                                    .message(errors.toString())
                                    .timestamp(Instant.now())
                                    .path(request.getDescription(false))
                                    .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Returns 400 Bad Request.
   * Handles illegal arguments.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {

    log.warn("Illegal argument for request to {}: {}", request.getDescription(false), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(HttpStatus.BAD_REQUEST.value())
                                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                    .message(ex.getMessage())
                                    .timestamp(Instant.now())
                                    .path(request.getDescription(false))
                                    .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * 401 Unauthorized.
   */
  @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
  public ResponseEntity<ErrorResponse> handleAuthenticationExceptions(Exception ex, WebRequest request) {

    log.warn("Authentication failed for request to {}: {}", request.getDescription(false), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(HttpStatus.UNAUTHORIZED.value())
                                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                                    .message("Invalid credentials or expired session")
                                    .timestamp(Instant.now())
                                    .path(request.getDescription(false))
                                    .build();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  /**
   * 403 Forbidden.
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {

    log.warn("Access denied for request to {}: {}", request.getDescription(false), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(HttpStatus.FORBIDDEN.value())
                                    .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                                    .message("This resource is out of your role's scope")
                                    .timestamp(Instant.now())
                                    .path(request.getDescription(false))
                                    .build();

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  /**
   * 500 Internal Server Error.
   * Handles unexpected exceptions.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {

    log.error("Unexpected error for request to {}", request.getDescription(false), ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                    .message("Oops! Unexpected error occurred")
                                    .timestamp(Instant.now())
                                    .path(request.getDescription(false))
                                    .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}