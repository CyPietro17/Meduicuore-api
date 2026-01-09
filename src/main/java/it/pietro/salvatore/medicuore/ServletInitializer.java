package it.pietro.salvatore.medicuore;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

  /**
   * This class is only needed if you deploy the application as a WAR on an external server
   */
  @Override
  @NullMarked
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(MedicuoreApplication.class);
  }

}
