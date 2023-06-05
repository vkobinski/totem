package com.kodev.totem.repositories;

import com.kodev.totem.models.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
}
