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

        List<Paciente> pacientes = pacienteRepository.getPacientesByNomeCompletoContainingIgnoreCaseAndDataNascimentoAndAtivoIsTrue(paciente.getNomeCompleto(), paciente.getDataNascimento());

        if(!pacientes.isEmpty()) return null;

        return pacienteRepository.save(paciente);
    }
    
    public Paciente edita(Paciente paciente) {

        Optional<Paciente> find = pacienteRepository.findById(paciente.getPacienteId());

        if(find.isPresent()) {
            Paciente pacienteFind = find.get();
            pacienteFind.setNomeCompleto(paciente.getNomeCompleto());
            pacienteFind.setDataNascimento(paciente.getDataNascimento());

            pacienteRepository.save(pacienteFind);

            return pacienteFind;
        }

        return null;
    }

    public List<Paciente> getPacientes() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        return pacientes;
    }

    public List<Paciente> getPacientesAtivos() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        pacientes.removeIf((p) -> {
            return !p.isAtivo();
        });
        return pacientes;
    }


    public Paciente deletePaciente(Long id) {
        Optional<Paciente> pacId = pacienteRepository.findById(id);
        return pacId.map(paciente -> {
            paciente.setAtivo(false);
            return paciente;
        }).orElseThrow(EntityNotFoundException::new);
    }

    public boolean changeStatus(Long idPaciente) {
        Optional<Paciente> find = pacienteRepository.findById(idPaciente);

        if(find.isPresent()) {
            Paciente paciente = find.get();
            paciente.setAtivo(!paciente.isAtivo());
            pacienteRepository.save(paciente);

            return true;
        }

        return false;
    }
    public List<Paciente> getPacientesByNomeCompletoContaining(String nome) {
        return pacienteRepository.getPacienteByNomeCompletoContainingIgnoreCase(nome);
    }

    public Paciente getPacientesByNomeCompleto(String nome) {
        return pacienteRepository.getPacienteByNomeCompleto(nome);
    }

    public List<Paciente> getPacientesByPrimeiraLetra(String letra) {
        return pacienteRepository.getPacienteByNomeCompletoStartingWithIgnoreCase(letra);
    }
}
