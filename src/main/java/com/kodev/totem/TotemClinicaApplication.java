package com.kodev.totem;

import com.kodev.totem.models.Usuario;
import com.kodev.totem.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TotemClinicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TotemClinicaApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository) {

		return (args) -> {
			Usuario usuario = new Usuario();
			usuario.setEmail("teste@gmail.com");
			usuario.setPassword("teste");
			usuarioRepository.save(usuario);
		};
	}
}
