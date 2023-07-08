package com.kodev.totem.repositories;

import com.kodev.totem.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    public Optional<Paciente> findPacienteByNomeCompletoIgnoreCase(String nome);

    public List<Paciente> getPacienteByNomeCompletoContainingIgnoreCase(String nome);

    public List<Paciente> getPacienteByNomeCompletoStartingWithIgnoreCase(String firstLetter);
}

