package com.pesagem_graos_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TipoGraoDTO {

    private Long id;

    @NotBlank
    private String nome;

    @NotNull
    @Positive
    private BigDecimal precoComprarPorTonelada;

    @NotNull
    @Positive
    private BigDecimal estoqueAtualToneladas;

    @NotNull
    @Positive
    private BigDecimal estoqueMaximoToneladas;
}