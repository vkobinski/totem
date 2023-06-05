package com.kodev.totem.repositories;

import com.kodev.totem.models.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
}
