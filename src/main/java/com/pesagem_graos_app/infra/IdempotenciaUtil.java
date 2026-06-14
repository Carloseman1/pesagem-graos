package com.pesagem_graos_app.infra;

public class IdempotenciaUtil {

    private static final java.util.concurrent.atomic.AtomicLong contador = new java.util.concurrent.atomic.AtomicLong(
            0);

    public static String gerarChave(String balancaId, String placa, double peso) {
        return balancaId + ":" + placa + ":" + String.format("%.2f", peso)
                + ":" + contador.getAndIncrement();
    }
}