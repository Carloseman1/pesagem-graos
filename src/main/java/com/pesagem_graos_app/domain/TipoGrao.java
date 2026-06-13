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
@Table(name = "tipo_grao")
@Data
public class TipoGrao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_grao_seq")
    @SequenceGenerator (name="tipo_grao_seq", sequenceName="tipo_grao_seq", allocationSize=1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;
    
    @Column(nullable=false)
    private BigDecimal precoComprarPorTonelada;

    @Column(nullable = false)
    private BigDecimal estoqueAtualToneladas;

    @Column(nullable = false)
    private BigDecimal estoqueMaximoToneladas;

}
