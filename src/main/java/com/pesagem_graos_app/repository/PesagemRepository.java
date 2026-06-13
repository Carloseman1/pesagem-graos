package com.pesagem_graos_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pesagem_graos_app.domain.Pesagem;




public interface PesagemRepository extends JpaRepository<Pesagem, Long>{
    
}
