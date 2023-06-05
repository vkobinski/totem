package com.kodev.totem.repositories;

import com.kodev.totem.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
