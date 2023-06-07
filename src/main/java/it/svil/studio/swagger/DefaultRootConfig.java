package it.svil.studio.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DefaultRootConfig implements WebMvcConfigurer {

    /**
     * Shows Swagger page on default route on startup.
     * @param registry view's registry.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/medicuore-swagger-ui.html");
    }
}
