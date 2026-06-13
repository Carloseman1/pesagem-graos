package com.pesagem_graos_app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.pesagem_graos_app.exception.RegraDeNegocioException;

@Service
public class PrecificacaoService {

    private static final BigDecimal MARGEM_MINIMA = new BigDecimal("0.05");
    private static final BigDecimal MARGEM_MAXIMA = new BigDecimal("0.20");

    public BigDecimal calcularMargem(BigDecimal estoqueAtual, BigDecimal estoqueMaximo) {

        if (estoqueMaximo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Estoque máximo deve ser maior que zero");
        }

        BigDecimal proporcao = estoqueAtual.divide(estoqueMaximo, 10, RoundingMode.HALF_UP)
                .min(BigDecimal.ONE);

        return MARGEM_MAXIMA.subtract(proporcao.multiply(MARGEM_MAXIMA.subtract(MARGEM_MINIMA)))
                .setScale(10, RoundingMode.HALF_UP);
    }

    public BigDecimal calculaPrecoVenda(BigDecimal precoCompra, BigDecimal margem) {
        return precoCompra.multiply(BigDecimal.ONE.add(margem))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularCustoDaCarga(BigDecimal pesoLiquidoToneladas, BigDecimal precoCompraPorTonelada) {
        return pesoLiquidoToneladas.multiply(precoCompraPorTonelada)
                .setScale(2, RoundingMode.HALF_UP);
    }
}