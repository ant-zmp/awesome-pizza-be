package com.portfolio.awesomepizzabe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AwesomePizzaBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwesomePizzaBeApplication.class, args);
	}

}
