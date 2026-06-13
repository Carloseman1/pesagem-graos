package com.pesagem_graos_app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "caminhao")
@Data
public class Caminhao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "caminhao_seq")
    @SequenceGenerator(name = "caminhao_seq", sequenceName = "caminhao_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tara;

    @Column(nullable= false)
    private String motorista;
}
