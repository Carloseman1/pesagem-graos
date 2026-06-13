package com.pesagem_graos_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pesagem_graos_app.domain.Caminhao;

public interface CaminhaoRepository extends JpaRepository<Caminhao, Long> {
    
    Optional<Caminhao> findByPlaca(String placa);
}
