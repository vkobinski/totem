package com.kodev.totem.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Table
@Entity
@Getter
@Setter
public class Atendimento {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long atendimentoId;

    @ManyToOne
    @JoinColumn(name = "paciente_atendimento_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_atendimento_id")
    private Medico medico;

    @Column
    private LocalDateTime dataAtendimento;

    @Column
    private boolean chegou = false;
}
