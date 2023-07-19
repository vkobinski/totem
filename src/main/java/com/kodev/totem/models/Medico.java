package com.kodev.totem.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.MapKeyCompositeType;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import javax.imageio.ImageTypeSpecifier;
import java.sql.Types;

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
    @JdbcType(VarbinaryJdbcType.class)
    @Column(name = "foto")
    private byte[] foto;

    @Column
    private boolean ativo = true;
}
