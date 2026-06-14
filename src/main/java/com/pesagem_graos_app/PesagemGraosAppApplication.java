package com.pesagem_graos_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PesagemGraosAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PesagemGraosAppApplication.class, args);
	}

}
