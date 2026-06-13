package com.pesagem_graos_app.infra;

public class IdempotenciaUtil {

    public static String gerarChave(String balancaId, String placa, double peso) {
        return balancaId + ":" + placa + ":" + String.format("%.2f", peso);
    }
}