package com.kodev.totem.repositories;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    public Optional<Medico> findMedicoByNomeCompletoIgnoreCase(String nome);
}
