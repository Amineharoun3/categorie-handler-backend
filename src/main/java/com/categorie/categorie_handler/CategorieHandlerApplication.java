package com.categorie.categorie_handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com/categorie/categorie_handler/model")
@EnableJpaRepositories("com/categorie/categorie_handler/repository")

public class CategorieHandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CategorieHandlerApplication.class, args);
	}
}
