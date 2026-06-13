package com.pesagem_graos_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pesagem_graos_app.domain.Balanca;

public interface BalancaRepository extends JpaRepository<Balanca, Long>{
    
    Optional<Balanca> findByIdentificadorAndApiKey(String identificador, String apiKey);
    Optional<Balanca> findByIdentificador(String identificador);
}
