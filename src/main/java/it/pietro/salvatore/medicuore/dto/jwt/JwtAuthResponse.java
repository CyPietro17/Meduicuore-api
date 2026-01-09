package it.pietro.salvatore.medicuore.dto.jwt;

import it.pietro.salvatore.medicuore.dto.response.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO per la risposta di autenticazione JWT.
 * <p>
 * Contiene:
 * - Access token per le richieste API
 * - Refresh token per rinnovare l'access token
 * - Informazioni sull'utente autenticato
 * - Metadata sui token (scadenza, tipo)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse implements ResponseDto {

  /**
   * Access token JWT per autenticare le richieste API.
   * Durata breve (default 15 minuti).
   */
  private String accessToken;

  /**
   * Refresh token JWT per ottenere un nuovo access token.
   * Durata lunga (default 7 giorni).
   */
  private String refreshToken;

  /**
   * Tipo di token (sempre "Bearer" per JWT).
   */
  @Builder.Default
  private String tokenType = "Bearer";

  /**
   * Username dell'utente autenticato.
   */
  private String username;

  /**
   * Email dell'utente (opzionale).
   */
  private String email;

  /**
   * Ruoli assegnati all'utente.
   */
  private List<String> roles;

  /**
   * Timestamp di scadenza dell'access token.
   */
  private Instant accessTokenExpiresAt;

  /**
   * Timestamp di scadenza del refresh token.
   */
  private Instant refreshTokenExpiresAt;
}
