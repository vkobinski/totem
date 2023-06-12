package com.kodev.totem;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Medico;
import com.kodev.totem.models.Paciente;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.repositories.AtendimentoRepository;
import com.kodev.totem.repositories.MedicoRepository;
import com.kodev.totem.repositories.PacienteRepository;
import com.kodev.totem.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

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


			Paciente paciente = new Paciente();
			paciente.setNomeCompleto("Victor");

			SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy");
			java.util.Date parse = dateTimeFormatter.parse("18/05/2004");
			java.sql.Date data = new java.sql.Date(parse.getTime());

			paciente.setDataNascimento(data);
			paciente.setAtivo(true);
			Paciente save1 = pacienteRepository.save(paciente);

			System.out.println(save1.getPacienteId());

			Medico medico = new Medico();
			medico.setAtivo(true);
			medico.setNomeCompleto("Victor Médico");

			Medico save = medicoRepository.save(medico);

			Usuario usuario = new Usuario();
			usuario.setEmail("teste@gmail.com");
			usuario.setPassword("teste");
			usuario.setMedico(save);
			usuarioRepository.save(usuario);

			Atendimento atendimento  = new Atendimento();
			atendimento.setMedico(save);
			atendimento.setPaciente(save1);

			LocalDateTime currentDate = LocalDateTime.now();

			atendimento.setDataAtendimento(currentDate);

			atendimento.setChegou(false);

			atendimentoRepository.save(atendimento);

		};
	}
}
