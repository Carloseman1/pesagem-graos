package com.pesagem_graos_app.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "leitura_recebida")
@Data
public class LeituraRecebida {

    @Id
    private String chave;

    @Column(nullable = false)
    private LocalDateTime recebidaEm;
}
