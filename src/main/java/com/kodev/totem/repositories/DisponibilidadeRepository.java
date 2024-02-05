package com.kodev.totem.repositories;

import com.kodev.totem.models.Disponibilidade;
import com.kodev.totem.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {

    public List<Disponibilidade> getAllByDia(Date dia);
    public List<Disponibilidade> getAllByDiaAndMedico_MedicoId(Date dia, Long medicoId);
}
