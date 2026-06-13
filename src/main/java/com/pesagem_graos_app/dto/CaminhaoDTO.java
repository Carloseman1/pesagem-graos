package com.pesagem_graos_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CaminhaoDTO {

    private Long id;

    @NotBlank
    private String placa;

    @NotNull
    @Positive
    private BigDecimal tara;

    @NotBlank
    private String motorista;
}