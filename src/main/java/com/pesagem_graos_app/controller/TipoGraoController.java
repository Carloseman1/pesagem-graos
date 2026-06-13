package com.pesagem_graos_app.controller;

import com.pesagem_graos_app.domain.TipoGrao;
import com.pesagem_graos_app.dto.TipoGraoDTO;
import com.pesagem_graos_app.repository.TipoGraoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-grao")
@RequiredArgsConstructor
public class TipoGraoController {

    private final TipoGraoRepository tipoGraoRepository;

    @GetMapping
    public List<TipoGrao> listar() {
        return tipoGraoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<TipoGrao> criar(@RequestBody @Valid TipoGraoDTO dto) {
        TipoGrao tipo = new TipoGrao();
        tipo.setNome(dto.getNome());
        tipo.setPrecoComprarPorTonelada(dto.getPrecoComprarPorTonelada());
        tipo.setEstoqueAtualToneladas(dto.getEstoqueAtualToneladas());
        tipo.setEstoqueMaximoToneladas(dto.getEstoqueMaximoToneladas());
        return ResponseEntity.status(201).body(tipoGraoRepository.save(tipo));
    }
}