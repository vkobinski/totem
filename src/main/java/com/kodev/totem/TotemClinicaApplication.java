package com.kodev.totem;

import com.kodev.totem.repositories.AtendimentoRepository;
import com.kodev.totem.repositories.MedicoRepository;
import com.kodev.totem.repositories.PacienteRepository;
import com.kodev.totem.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TotemClinicaApplication {
	private final AtendimentoRepository atendimentoRepository;

	public TotemClinicaApplication(AtendimentoRepository atendimentoRepository) {
		this.atendimentoRepository = atendimentoRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(TotemClinicaApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository, PacienteRepository pacienteRepository, MedicoRepository medicoRepository) {

		return (args) -> {
		};
	}
}
