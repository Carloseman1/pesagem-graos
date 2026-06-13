package com.pesagem_graos_app.controller;

import com.pesagem_graos_app.domain.Caminhao;
import com.pesagem_graos_app.dto.CaminhaoDTO;
import com.pesagem_graos_app.repository.CaminhaoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/caminhoes")
@RequiredArgsConstructor
public class CaminhaoController {

    private final CaminhaoRepository caminhaoRepository;

    @GetMapping
    public List<Caminhao> listar() {
        return caminhaoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Caminhao> criar(@RequestBody @Valid CaminhaoDTO dto) {
        Caminhao caminhao = new Caminhao();
        caminhao.setPlaca(dto.getPlaca());
        caminhao.setTara(dto.getTara());
        caminhao.setMotorista(dto.getMotorista());
        return ResponseEntity.status(201).body(caminhaoRepository.save(caminhao));
    }
}