package it.pietro.salvatore.medicuore.config.exception;

import it.pietro.salvatore.medicuore.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

import static it.pietro.salvatore.medicuore.utils.HttpResponseStatus.*;

/**
 * Gestore globale delle eccezioni per l'applicazione.
 * <p>
 * Intercetta tutte le eccezioni non gestite nei controller e restituisce
 * risposte di errore standardizzate in formato JSON.
 * <p>
 * Vantaggi:
 * - Gestione centralizzata degli errori
 * - Risposte consistenti in tutta l'applicazione
 * - Logging automatico degli errori
 * - Nessun codice try-catch nei controller
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Gestisce errori di validazione dei parametri (es. @NotBlank, @Size).
   * Restituisce 400 Bad Request con dettagli sui campi non validi.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {

    String errorMessage = ex.getBindingResult().getAllErrors().stream().map(error -> {
      String fieldName = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      return String.format("%s: %s", fieldName, message);
    }).collect(Collectors.joining(", "));

    log.warn("Validation error on {}: {}", request.getRequestURI(), errorMessage);

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(BAD_REQUEST_STATUS.value())
                                    .error("Validation Failed")
                                    .message(errorMessage)
                                    .timestamp(Instant.now())
                                    .path(request.getRequestURI())
                                    .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Gestisce errori di autenticazione (credenziali errate).
   * Restituisce 401 Unauthorized.
   */
  @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
  public ResponseEntity<ErrorResponse> handleAuthenticationExceptions(Exception ex, HttpServletRequest request) {

    log.warn("Authentication failed for request to {}: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(UNAUTHORIZED_STATUS.value())
                                    .error(UNAUTHORIZED_STATUS.getReasonPhrase())
                                    .message("Invalid credentials or expired session")
                                    .timestamp(Instant.now())
                                    .path(request.getRequestURI())
                                    .build();

    return ResponseEntity.status(UNAUTHORIZED_STATUS).body(errorResponse);
  }

  /**
   * Gestisce errori di autorizzazione (accesso negato per mancanza di permessi).
   * Restituisce 403 Forbidden.
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {

    log.warn("Access denied for request to {}: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(FORBIDDEN_STATUS.value())
                                    .error(FORBIDDEN_STATUS.getReasonPhrase())
                                    .message("Non hai i permessi necessari per accedere a questa risorsa")
                                    .timestamp(Instant.now())
                                    .path(request.getRequestURI())
                                    .build();

    return ResponseEntity.status(FORBIDDEN_STATUS).body(errorResponse);
  }

  /**
   * Gestisce argomenti illegali (es. dati non validi).
   * Restituisce 400 Bad Request.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {

    log.warn("Illegal argument for request to {}: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(BAD_REQUEST_STATUS.value())
                                    .error(BAD_REQUEST_STATUS.getReasonPhrase())
                                    .message(ex.getMessage())
                                    .timestamp(Instant.now())
                                    .path(request.getRequestURI())
                                    .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Gestisce tutte le altre eccezioni non previste.
   * Restituisce 500 Internal Server Error.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

    log.error("Unexpected error for request to {}", request.getRequestURI(), ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(INTERNAL_SERVER_ERROR_STATUS.value())
                                    .error(INTERNAL_SERVER_ERROR_STATUS.getReasonPhrase())
                                    .message("Si è verificato un errore interno del server")
                                    .timestamp(Instant.now())
                                    .path(request.getRequestURI())
                                    .build();

    return ResponseEntity.status(INTERNAL_SERVER_ERROR_STATUS).body(errorResponse);
  }
}