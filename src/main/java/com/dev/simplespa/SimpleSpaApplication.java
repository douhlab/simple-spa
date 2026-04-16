package com.dev.simplespa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SimpleSpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleSpaApplication.class, args);
	}

}
