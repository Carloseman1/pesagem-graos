package com.pesagem_graos_app.service;

import org.springframework.stereotype.Service;

import com.pesagem_graos_app.domain.Balanca;
import com.pesagem_graos_app.exception.BalancaNaoAutorizadaException;
import com.pesagem_graos_app.repository.BalancaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalancaService {

    private final BalancaRepository balancaRepository;

    public Balanca validarApiKey(String identificador, String apiKey) {
        return balancaRepository.findByIdentificadorAndApiKey(identificador, apiKey)
                .orElseThrow(() -> new BalancaNaoAutorizadaException(identificador));
    }
}
