package it.svil.studio.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private List<SecurityContext> securityContexts() {
        return Arrays.asList(SecurityContext.builder().securityReferences(references()).build());
    }

    private List<SecurityReference> references(){
        AuthorizationScope scope = new AuthorizationScope("global", "Access Everything");
        return Arrays.asList(new SecurityReference("JWT", new AuthorizationScope[] {scope}));
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("MediCuore API")
                        .description("MediCuore RESTful Web service documentation")
                        .version("v1.0.0")
                        .license(new License().name("SpringDoc 2.0").url("http://springdoc.org")));
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select().paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("it.svil.studio"))
                .build()
                .securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(securityContexts())
                .apiInfo(apiInfo())
                .pathMapping("/")
                .useDefaultResponseMessages(false)
                .directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class);
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("MediCuore")
                .version("1.0.0").description("La clinica a portata di tutti")
                .contact(new Contact("Pietro Salvatore", "itsvil.it", "pietro.salvatore@itsvil.it"))
                .build();
    }

}
