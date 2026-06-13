package com.pesagem_graos_app.exception;

public class PesagemNaoEstabilizadaException extends RuntimeException {
    public PesagemNaoEstabilizadaException(String mensagem) {
        super(mensagem);
    }
}