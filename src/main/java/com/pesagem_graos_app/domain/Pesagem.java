package com.pesagem_graos_app.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name="pesagem")
@Data
public class Pesagem {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="pesagem_seq")
    @SequenceGenerator(name="pesagem_seq", sequenceName="pesagem_seq", allocationSize=1)
    private Long id;

    @Column(nullable=false)
    private String placa;

    @Column(nullable=false)
    private BigDecimal pesoBrutoEstabilizado;

    @Column(nullable=false)
    private BigDecimal tara;

    @Column(nullable=false)
    private BigDecimal pesoLiquido;

    @Column(nullable=false)
    private LocalDateTime dataHoraPesagem;

    @Column(nullable=false)
    private BigDecimal custoDaCarga;

    @ManyToOne
    @JoinColumn(nullable=false)
    private Balanca balanca;

    @ManyToOne
    @JoinColumn(nullable=false)
    private TipoGrao tipoGrao;
}
