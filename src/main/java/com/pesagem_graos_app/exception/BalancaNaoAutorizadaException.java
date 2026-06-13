package com.pesagem_graos_app.exception;

public class BalancaNaoAutorizadaException extends RuntimeException {
    public BalancaNaoAutorizadaException(String identificador) {
        super("Balança não autorizada: " + identificador);
    }
}