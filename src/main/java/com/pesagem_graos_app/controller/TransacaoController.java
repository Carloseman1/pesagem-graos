package com.pesagem_graos_app.controller;

import com.pesagem_graos_app.domain.*;
import com.pesagem_graos_app.dto.TransacaoRequestDTO;
import com.pesagem_graos_app.exception.CaminhaoNaoEncontradoException;
import com.pesagem_graos_app.exception.RegraDeNegocioException;
import com.pesagem_graos_app.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoRepository transacaoRepository;
    private final CaminhaoRepository caminhaoRepository;
    private final TipoGraoRepository tipoGraoRepository;
    private final FilialRepository filialRepository;

    @GetMapping
    public List<TransacaoTransporte> listar() {
        return transacaoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<TransacaoTransporte> iniciar(@RequestBody @Valid TransacaoRequestDTO dto) {
        Caminhao caminhao = caminhaoRepository.findByPlaca(dto.getPlacaCaminhao())
                .orElseThrow(() -> new CaminhaoNaoEncontradoException(dto.getPlacaCaminhao()));

        transacaoRepository.findByCaminhaoPlacaAndStatus(dto.getPlacaCaminhao(), StatusTransacao.EM_ANDAMENTO)
                .ifPresent(t -> { throw new RegraDeNegocioException(
                        "Caminhão já possui uma transação em andamento"); });

        TipoGrao tipoGrao = tipoGraoRepository.findById(dto.getIdTipoGrao())
                .orElseThrow(() -> new RegraDeNegocioException("Tipo de grão não encontrado: " + dto.getIdTipoGrao()));

        Filial filial = filialRepository.findById(dto.getIdFilial())
                .orElseThrow(() -> new RegraDeNegocioException("Filial não encontrada: " + dto.getIdFilial()));

        TransacaoTransporte transacao = new TransacaoTransporte();
        transacao.setCaminhao(caminhao);
        transacao.setTipoGrao(tipoGrao);
        transacao.setFilial(filial);
        transacao.setStatus(StatusTransacao.EM_ANDAMENTO);
        transacao.setInicio(LocalDateTime.now());

        return ResponseEntity.status(201).body(transacaoRepository.save(transacao));
    }
}