package com.kodev.totem.services;

import com.kodev.totem.models.Paciente;
import com.kodev.totem.repositories.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
