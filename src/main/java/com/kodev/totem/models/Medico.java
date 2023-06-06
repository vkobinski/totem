package com.kodev.totem.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.MapKeyCompositeType;
import org.hibernate.annotations.Type;

import javax.imageio.ImageTypeSpecifier;

@Table
@Entity
@Getter
@Setter
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long medicoId;

    @Column
    private String nomeCompleto;

    @Lob
    private byte[] foto;

    @Column
    private boolean ativo = true;
}
