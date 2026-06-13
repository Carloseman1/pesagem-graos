package com.pesagem_graos_app.service;

import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pesagem_graos_app.dto.LeituraDTO;
import com.pesagem_graos_app.exception.PesagemNaoEstabilizadaException;

@Service
public class EstabilizacaoService {

    @Value("${estabilizacao.janela-minima:10}")
    private int janelaMinima;

    @Value("${estabilizacao.threshold-kg:0.5}")
    private double thresholdKg;

    public boolean estaEstavel(Deque<LeituraDTO> leituras) {
        if (leituras.size() < janelaMinima) {
            return false;
        }

        List<Double> pesos = leituras.stream()
                .map(LeituraDTO::getPeso)
                .skip(Math.max(0, leituras.size() - janelaMinima))
                .collect(Collectors.toList());

        double media = calcularMedia(pesos);
        double desvio = calcularDesvioPadrao(pesos, media);

        return desvio < thresholdKg;
    }

    public double calcularPesoEstabilizado(Deque<LeituraDTO> leituras) {
        return leituras.stream()
                .map(LeituraDTO::getPeso)
                .skip(Math.max(0, leituras.size() - janelaMinima))
                .mapToDouble(Double::doubleValue)
                .average()
                .orElseThrow(() -> new PesagemNaoEstabilizadaException("Fila de leituras vazia"));
    }

    private double calcularMedia(List<Double> valores) {
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double calcularDesvioPadrao(List<Double> valores, double media) {
        double variancia = valores.stream()
                .mapToDouble(v -> Math.pow(v - media, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variancia);
    }
}