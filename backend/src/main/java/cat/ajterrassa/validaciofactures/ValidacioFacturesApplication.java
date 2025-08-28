package cat.ajterrassa.validaciofactures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ValidacioFacturesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidacioFacturesApplication.class, args);
	}

	
	// Aquesta funció s'executarà al iniciar l'aplicació i generarà un hash de la contrasenya "admin123"
	//per obtenir-la i posar-la a la base de dades directe
	//es un log de testing per la contrasenya encirptada i poder copiar a DB i accedir amb un usuari admin
	@Bean
	public CommandLineRunner testHash() {
		return args -> {
			String raw = "admin123";
			String hash = new BCryptPasswordEncoder().encode(raw);
			System.out.println("🧪 HASH generat: " + hash);
		};
	}
}

