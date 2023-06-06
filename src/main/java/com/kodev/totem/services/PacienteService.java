package com.kodev.totem.services;

import com.kodev.totem.models.Paciente;
import com.kodev.totem.repositories.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Paciente criaPaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public List<Paciente> getPacientes() {
        return pacienteRepository.findAll();
    }

    public Paciente deletePaciente(Long id) {
        Optional<Paciente> pacId = pacienteRepository.findById(id);
        return pacId.map(paciente -> {
            paciente.setAtivo(false);
            return paciente;
        }).orElseThrow(EntityNotFoundException::new);
    }
}
