package com.pesagem_graos_app.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transacao_transporte")
@Data
public class TransacaoTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transacao_seq")
    @SequenceGenerator(name = "transacao_seq", sequenceName = "transacao_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    @Column(nullable = false)
    private LocalDateTime inicio;

    private LocalDateTime fim;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Caminhao caminhao;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TipoGrao tipoGrao;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Filial filial;

    @OneToOne
    @JoinColumn
    private Pesagem pesagem;
}
