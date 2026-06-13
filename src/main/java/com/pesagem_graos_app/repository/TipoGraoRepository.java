package com.pesagem_graos_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pesagem_graos_app.domain.TipoGrao;

public interface TipoGraoRepository extends JpaRepository<TipoGrao, Long>{
    
}
