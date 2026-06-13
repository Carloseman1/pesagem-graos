package com.pesagem_graos_app.controller;

import java.util.List;

import com.pesagem_graos_app.domain.Pesagem;
import com.pesagem_graos_app.dto.LeituraDTO;
import com.pesagem_graos_app.infra.FilaDePesagem;
import com.pesagem_graos_app.infra.IdempotenciaUtil;
import com.pesagem_graos_app.repository.PesagemRepository;
import com.pesagem_graos_app.service.BalancaService;
import com.pesagem_graos_app.service.IdempotenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PesagemController {

    private final BalancaService balancaService;
    private final IdempotenciaService idempotenciaService;
    private final FilaDePesagem filaDePesagem;
    private final PesagemRepository pesagemRepository;

    @GetMapping("/pesagens")
    public List<Pesagem> listar() {
        return pesagemRepository.findAll();
    }

    @PostMapping("/pesagem")
    public ResponseEntity<Void> receberLeitura(
            @RequestHeader("X-Balance-Key") String apiKey,
            @RequestBody LeituraDTO dto) {

        balancaService.validarApiKey(dto.getId(), apiKey);

        String chave = IdempotenciaUtil.gerarChave(dto.getId(), dto.getPlaca(), dto.getPeso());

        if (idempotenciaService.jaProcessado(chave)) {
            return ResponseEntity.accepted().build();
        }

        idempotenciaService.registrar(chave);
        filaDePesagem.enfileirar(dto);

        return ResponseEntity.accepted().build();
    }
}