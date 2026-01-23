package it.pietro.salvatore.medicuore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login request.
 * Contains user credentials (username and password).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request with username and password")
public class LoginRequestDto {

  @NotBlank(message = "Username is mandatory")
  @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
  @Schema(
    description = "User's username",
    example = "admin",
    requiredMode = Schema.RequiredMode.REQUIRED,
    minLength = 3,
    maxLength = 20
  )
  private String username;

  @NotBlank(message = "Password is mandatory")
  @Size(min = 6, message = "Password must be at least 6 characters")
  @Schema(
    description = "User's password",
    example = "Studio55",
    requiredMode = Schema.RequiredMode.REQUIRED,
    minLength = 6,
    format = "password"
  )
  private String password;
}