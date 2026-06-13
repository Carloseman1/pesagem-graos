package com.pesagem_graos_app.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pesagem_graos_app.exception.RegraDeNegocioException;
import com.pesagem_graos_app.infra.FilaDePesagem;
import com.pesagem_graos_app.repository.BalancaRepository;
import com.pesagem_graos_app.service.EstabilizacaoService;
import com.pesagem_graos_app.service.PesagemService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EstabilizacaoScheduler {

    private static final Logger log = LoggerFactory.getLogger(EstabilizacaoScheduler.class);

    private final FilaDePesagem filaDePesagem;
    private final EstabilizacaoService estabilizacaoService;
    private final PesagemService pesagemService;
    private final BalancaRepository balancaRepository;

    @Scheduled(fixedDelay = 200)
    public void processarFilas() {
        filaDePesagem.obterTodasFilas().forEach((balancaId, fila) -> {
            if (fila.isEmpty()) return;

            if (estabilizacaoService.estaEstavel(fila)) {
                double pesoEstabilizado = estabilizacaoService.calcularPesoEstabilizado(fila);

                balancaRepository.findByIdentificador(balancaId).ifPresent(balanca -> {
                    try {
                        pesagemService.salvarPesagemEstabilizada(
                                balancaId, balanca, fila.peekLast(), pesoEstabilizado);
                        filaDePesagem.limparFila(balancaId);
                    } catch (RegraDeNegocioException e) {
                        log.warn("Peso estabilizado na balança {}, mas não foi possível salvar: {}",
                                balancaId, e.getMessage());
                        filaDePesagem.limparFila(balancaId);
                    }
                });
            }
        });
    }
}