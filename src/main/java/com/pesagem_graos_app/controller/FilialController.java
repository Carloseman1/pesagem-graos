package com.pesagem_graos_app.controller;

import com.pesagem_graos_app.domain.Filial;
import com.pesagem_graos_app.dto.FilialDTO;
import com.pesagem_graos_app.repository.FilialRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/filiais")
@RequiredArgsConstructor
public class FilialController {

    private final FilialRepository filialRepository;

    @GetMapping
    public List<Filial> listar() {
        return filialRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Filial> criar(@RequestBody @Valid FilialDTO dto) {
        Filial filial = new Filial();
        filial.setNome(dto.getNome());
        filial.setCidade(dto.getCidade());
        filial.setEstado(dto.getEstado());
        return ResponseEntity.status(201).body(filialRepository.save(filial));
    }
}