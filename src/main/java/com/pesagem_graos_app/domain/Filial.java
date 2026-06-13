package com.pesagem_graos_app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "filial")
@Data
public class Filial {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="filial_seq")
    @SequenceGenerator(name="filial_seq", sequenceName="filial_seq", allocationSize=1)
    private Long id;

    @Column(nullable=false)
    private String nome;

    @Column(nullable=false)
    private String cidade;

    @Column(nullable=false)
    private String estado;
}
