package com.pesagem_graos_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransacaoRequestDTO {

    @NotBlank
    private String placaCaminhao;

    @NotNull
    private Long idTipoGrao;

    @NotNull
    private Long idFilial;
}