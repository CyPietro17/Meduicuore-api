package it.pietro.salvatore.medicuore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableSwagger2
//@PropertySource("file:${catalina.home}/conf/anagrafica/application.properties")
public class MedicuoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(MedicuoreApplication.class, args);
  }

}
