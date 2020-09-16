package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.OpenAPI;

@SpringBootApplication
public class JasperExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(JasperExampleApplication.class, args);
	}
	
	@Bean
	public OpenAPI openAPI() {
	   return new OpenAPI();
	}


}
