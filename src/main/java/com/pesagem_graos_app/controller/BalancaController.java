package com.pesagem_graos_app.controller;

import com.pesagem_graos_app.domain.Balanca;
import com.pesagem_graos_app.domain.Filial;
import com.pesagem_graos_app.dto.BalancaDTO;
import com.pesagem_graos_app.exception.RegraDeNegocioException;
import com.pesagem_graos_app.repository.BalancaRepository;
import com.pesagem_graos_app.repository.FilialRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/balancas")
@RequiredArgsConstructor
public class BalancaController {

    private final BalancaRepository balancaRepository;
    private final FilialRepository filialRepository;

    @GetMapping
    public List<Balanca> listar() {
        return balancaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Balanca> criar(@RequestBody @Valid BalancaDTO dto) {
        Filial filial = filialRepository.findById(dto.getIdFilial())
                .orElseThrow(() -> new RegraDeNegocioException("Filial não encontrada: " + dto.getIdFilial()));

        Balanca balanca = new Balanca();
        balanca.setIdentificador(dto.getIdentificador());
        balanca.setApiKey(dto.getApiKey());
        balanca.setFilial(filial);
        return ResponseEntity.status(201).body(balancaRepository.save(balanca));
    }
}