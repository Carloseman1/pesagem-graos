package com.pesagem_graos_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pesagem_graos_app.domain.StatusTransacao;
import com.pesagem_graos_app.domain.TransacaoTransporte;

public interface TransacaoRepository extends JpaRepository<TransacaoTransporte, Long> {

    Optional<TransacaoTransporte> findByCaminhaoPlacaAndStatus(String placa, StatusTransacao status);
}
