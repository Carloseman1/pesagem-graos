package com.pesagem_graos_app.exception;

public class CaminhaoNaoEncontradoException extends RuntimeException {
    public CaminhaoNaoEncontradoException(String placa) {
        super("Caminhão não encontrado para a placa: " + placa);
    }
}