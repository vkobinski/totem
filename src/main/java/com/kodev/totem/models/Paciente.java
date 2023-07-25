package com.kodev.totem.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Table(name = "paciente")
@Entity
@Getter
@Setter
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pacienteId;

    @Column(nullable = false)
    private String nomeCompleto;
    @Column(nullable = false)
    private Date dataNascimento;

    @Column
    private boolean ativo = true;
}
