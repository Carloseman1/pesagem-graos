package com.pesagem_graos_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilialDTO {

    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 2)
    private String estado;
}