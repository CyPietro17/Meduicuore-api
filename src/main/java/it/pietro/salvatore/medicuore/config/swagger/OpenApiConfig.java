package it.pietro.salvatore.medicuore.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for Swagger documentation.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Configures the application's OpenAPI documentation.
   * Includes general information and JWT configuration for authentication.
   *
   * @return OpenAPI configuration
   */
  @Bean
  public OpenAPI medicuoreOpenAPI() {
    return new OpenAPI()
             .info(new Info()
                     .title("MediCuore API")
                     .description("MediCuore RESTful Web service documentation")
                     .version("2.5.7")
                     .contact(new Contact()
                                .name("Pietro Salvatore")
                                .url("https://itsvil.it")
                                .email("pietro.salvatore@itsvil.it"))
                     .license(new License()
                                .name("SpringDoc 2.0")
                                .url("https://springdoc.org")))
             .components(new Components()
                           .addSecuritySchemes("JWT", new SecurityScheme()
                                                        .type(SecurityScheme.Type.HTTP)
                                                        .scheme("bearer")
                                                        .bearerFormat("JWT")
                                                        .in(SecurityScheme.In.HEADER)
                                                        .name("Authorization")
                                                        .description("Insert the JWT Token (Without 'Bearer ')")))
             .addSecurityItem(new SecurityRequirement()
                                .addList("JWT"));
  }
}