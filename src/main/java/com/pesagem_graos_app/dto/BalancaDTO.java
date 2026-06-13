package com.pesagem_graos_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BalancaDTO {

    private Long id;

    @NotBlank
    private String identificador;

    @NotBlank
    private String apiKey;

    @NotNull
    private Long idFilial;
}