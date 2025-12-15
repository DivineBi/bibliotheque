package fr.ensitech.biblio;

import fr.ensitech.biblio.repository.ISecurityQuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BiblioApplication {

	public static void main(String[] args) {

		SpringApplication.run(BiblioApplication.class, args);


	}

	@Bean
	CommandLineRunner testScan(ISecurityQuestionRepository sqRepo) {
		return args -> {
			System.out.println(">>> Nombre de questions secrètes trouvées : " + sqRepo.count());
		};
	}

}
