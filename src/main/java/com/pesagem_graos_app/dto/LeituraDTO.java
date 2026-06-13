package com.pesagem_graos_app.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class LeituraDTO {

    private String id;
    @JsonAlias("plate")
    private String placa;
    @JsonAlias("weight")
    private double peso;
}