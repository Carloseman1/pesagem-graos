package com.pesagem_graos_app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "balanca")
@Data
public class Balanca {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balanca_seq")
    @SequenceGenerator(name = "balanca_seq", sequenceName = "balanca_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificador;

    @Column(nullable=false, unique=true)
    private String apiKey;

    @ManyToOne
    @JoinColumn(nullable=false)
    private Filial filial;


}
