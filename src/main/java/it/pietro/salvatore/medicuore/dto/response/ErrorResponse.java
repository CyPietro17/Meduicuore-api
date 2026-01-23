package it.pietro.salvatore.medicuore.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standardized DTO for error responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standardized error response")
public class ErrorResponse {

  @Schema(description = "HTTP status code", example = "400")
  private int status;

  @Schema(description = "HTTP error name", example = "Bad Request")
  private String error;

  @Schema(description = "Descriptive error message", example = "Invalid username or password")
  private String message;

  @Schema(description = "Error timestamp", example = "2025-01-04T10:30:00Z")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private Instant timestamp;

  @Schema(description = "Request path that generated the error", example = "/auth/login")
  private String path;
}