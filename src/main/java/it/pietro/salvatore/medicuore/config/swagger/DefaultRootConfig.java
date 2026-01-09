package it.pietro.salvatore.medicuore.config.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration.
 * Redirects the application root to the Swagger UI page.
 */
@Configuration
public class DefaultRootConfig implements WebMvcConfigurer {

  /**
   * Redirects the root URL (/) to the Swagger UI page.
   *
   * @param registry view controller registry
   */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/", "/swagger-ui.html");
  }
}