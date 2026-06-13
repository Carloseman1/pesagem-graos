package com.pesagem_graos_app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.pesagem_graos_app.domain.Balanca;
import com.pesagem_graos_app.domain.Caminhao;
import com.pesagem_graos_app.domain.Pesagem;
import com.pesagem_graos_app.domain.StatusTransacao;
import com.pesagem_graos_app.domain.TipoGrao;
import com.pesagem_graos_app.domain.TransacaoTransporte;
import com.pesagem_graos_app.dto.LeituraDTO;
import com.pesagem_graos_app.exception.CaminhaoNaoEncontradoException;
import com.pesagem_graos_app.exception.RegraDeNegocioException;
import com.pesagem_graos_app.repository.CaminhaoRepository;
import com.pesagem_graos_app.repository.PesagemRepository;
import com.pesagem_graos_app.repository.TipoGraoRepository;
import com.pesagem_graos_app.repository.TransacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PesagemService {

    private final PesagemRepository pesagemRepository;
    private final CaminhaoRepository caminhaoRepository;
    private final TransacaoRepository transacaoRepository;
    private final TipoGraoRepository tipoGraoRepository;
    private final PrecificacaoService precificacaoService;

    public void salvarPesagemEstabilizada(String balancaId, Balanca balanca,
            LeituraDTO ultimaLeitura, double pesoEstabilizado) {

        Caminhao caminhao = caminhaoRepository.findByPlaca(ultimaLeitura.getPlaca())
                .orElseThrow(() -> new CaminhaoNaoEncontradoException(ultimaLeitura.getPlaca()));

        TransacaoTransporte transacao = transacaoRepository
                .findByCaminhaoPlacaAndStatus(ultimaLeitura.getPlaca(), StatusTransacao.EM_ANDAMENTO)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Nenhuma transação em andamento para o caminhão: " + ultimaLeitura.getPlaca()));

        TipoGrao tipoGrao = transacao.getTipoGrao();

        BigDecimal pesoBruto = BigDecimal.valueOf(pesoEstabilizado).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pesoLiquido = pesoBruto.subtract(caminhao.getTara());

        BigDecimal margem = precificacaoService.calcularMargem(
                tipoGrao.getEstoqueAtualToneladas(),
                tipoGrao.getEstoqueMaximoToneladas());

        BigDecimal precoVenda = precificacaoService.calculaPrecoVenda(
                tipoGrao.getPrecoComprarPorTonelada(), margem);

        BigDecimal pesoLiquidoToneladas = pesoLiquido.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
        BigDecimal custo = precificacaoService.calcularCustoDaCarga(
                pesoLiquidoToneladas, precoVenda);

        tipoGrao.setEstoqueAtualToneladas(
                tipoGrao.getEstoqueAtualToneladas().add(pesoLiquidoToneladas));
        tipoGraoRepository.save(tipoGrao);

        Pesagem pesagem = new Pesagem();
        pesagem.setPlaca(ultimaLeitura.getPlaca());
        pesagem.setPesoBrutoEstabilizado(pesoBruto);
        pesagem.setTara(caminhao.getTara());
        pesagem.setPesoLiquido(pesoLiquido);
        pesagem.setDataHoraPesagem(LocalDateTime.now());
        pesagem.setCustoDaCarga(custo);
        pesagem.setBalanca(balanca);
        pesagem.setTipoGrao(tipoGrao);

        pesagemRepository.save(pesagem);

        transacao.setPesagem(pesagem);
        transacao.setStatus(StatusTransacao.CONCLUIDA);
        transacao.setFim(LocalDateTime.now());
        transacaoRepository.save(transacao);
    }
}