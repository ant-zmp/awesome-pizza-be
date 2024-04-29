package com.portfolio.awesomepizzabe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan("com.portfolio.awesomepizzabe.config")
public class AwesomePizzaBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwesomePizzaBeApplication.class, args);
	}

}
