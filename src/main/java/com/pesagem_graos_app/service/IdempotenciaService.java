package com.pesagem_graos_app.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.pesagem_graos_app.domain.LeituraRecebida;
import com.pesagem_graos_app.repository.IdempotenciaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdempotenciaService {
    
    private final IdempotenciaRepository idempotenciaRepository;

    public boolean jaProcessado(String chave){
        return idempotenciaRepository.existsById(chave);
    }

    public void registrar(String chave){
        LeituraRecebida leitura = new LeituraRecebida();
        leitura.setChave(chave);
        leitura.setRecebidaEm(LocalDateTime.now());
        idempotenciaRepository.save(leitura);
    }
}
