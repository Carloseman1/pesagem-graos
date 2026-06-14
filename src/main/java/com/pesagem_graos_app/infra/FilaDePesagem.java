package com.pesagem_graos_app.infra;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Component;

import com.pesagem_graos_app.dto.LeituraDTO;

@Component
public class FilaDePesagem {

    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<LeituraDTO>> filas =
            new ConcurrentHashMap<>();

    public void enfileirar(LeituraDTO leitura) {
        filas.computeIfAbsent(leitura.getId(), id -> new ConcurrentLinkedDeque<>())
            .addLast(leitura);
    }

    public Map<String, ConcurrentLinkedDeque<LeituraDTO>> obterTodasFilas() {
        return Collections.unmodifiableMap(filas);
    }

    public void limparFila(String balancaId) {
        filas.remove(balancaId);
    }
}