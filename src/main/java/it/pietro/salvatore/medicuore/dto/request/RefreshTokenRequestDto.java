package it.pietro.salvatore.medicuore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for token renewal request.
 * Contains the refresh token to be validated to obtain a new access token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to renew access token via refresh token")
public class RefreshTokenRequestDto {

  @NotBlank(message = "Refresh token is mandatory")
  @Schema(
    description = "Valid JWT refresh token",
    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  private String refreshToken;
}