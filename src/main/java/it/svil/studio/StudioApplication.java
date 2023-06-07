package it.svil.studio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
//@PropertySource("file:${catalina.home}/conf/anagrafica/application.properties")
public class StudioApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudioApplication.class, args);
	}

}
