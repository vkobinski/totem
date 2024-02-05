package com.kodev.totem.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Table(name = "disponibilidade")
@Entity
@Getter
@Setter
public class Disponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long disponibilidadeId;

    @Column(nullable = false)
    private Date dia;

    @ManyToOne
    @JoinColumn(name = "medico_atendimento_id")
    private Medico medico;

    @Column
    private int horaInicio;
    @Column
    private int horaFim;
    @Column
    private int minutoInicio;
    @Column
    private int minutoFim;

    @Transient
    private boolean atendimento = false;
}
