package com.kodev.totem.repositories;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    @Query(value = "SELECT a FROM Atendimento a WHERE a.medico.medicoId = :medicoId AND DATE_TRUNC('DAY', a.dataAtendimento) = DATE_TRUNC('DAY', CURRENT_TIMESTAMP)")
    public List<Atendimento> getAtendimentosByMedico_MedicoId_Today(Long medicoId);

    public List<Atendimento> getAtendimentosByMedico_MedicoId(Long medicoId);

    //@Query("SELECT a FROM Atendimento a WHERE DATE(a.dataAtendimento) = CURRENT_DATE")
    //Query para MySQL
    @Query(value = "SELECT a FROM Atendimento a WHERE DATE_TRUNC('DAY', a.dataAtendimento) = DATE_TRUNC('DAY', CURRENT_TIMESTAMP)")
    List<Atendimento> findAllByDataAtendimentoToday();
    List<Atendimento> findByPacienteAndDataAtendimentoBetween(Paciente paciente, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Atendimento> findAllByPaciente_NomeCompletoStartingWith(String letter);

    @Query(value = "SELECT a FROM Atendimento a WHERE LOWER(a.paciente.nomeCompleto) LIKE CONCAT(LOWER(:letter), '%') AND DATE_TRUNC('DAY', a.dataAtendimento) = DATE_TRUNC('DAY', CURRENT_TIMESTAMP)")
    List<Atendimento> findAllByPaciente_NomeCompletoStartingWithAndDataAtendimentoToday(@Param("letter") String letter);
}
