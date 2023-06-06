package com.kodev.totem.repositories;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    public List<Atendimento> getAtendimentosByMedico_MedicoId(Long medicoId);

    List<Atendimento> findByPacienteAndDataAtendimentoBetween(Paciente paciente, Timestamp startOfDay, Timestamp endOfDay);
}
