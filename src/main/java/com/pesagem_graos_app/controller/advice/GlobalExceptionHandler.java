package com.pesagem_graos_app.controller.advice;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pesagem_graos_app.dto.ErroDTO;
import com.pesagem_graos_app.exception.BalancaNaoAutorizadaException;
import com.pesagem_graos_app.exception.CaminhaoNaoEncontradoException;
import com.pesagem_graos_app.exception.RegraDeNegocioException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BalancaNaoAutorizadaException.class)
    public ResponseEntity<ErroDTO> handleBalancaNaoAutorizada(BalancaNaoAutorizadaException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroDTO("BALANCA_NAO_AUTORIZADA", ex.getMessage()));
    }

    @ExceptionHandler(CaminhaoNaoEncontradoException.class)
    public ResponseEntity<ErroDTO> handleCaminhaoNaoEncontrado(CaminhaoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroDTO("CAMINHAO_NAO_ENCONTRADO", ex.getMessage()));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroDTO> handleRegraDeNegocio(RegraDeNegocioException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErroDTO("REGRA_DE_NEGOCIO", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDTO> handleValidacao(MethodArgumentNotValidException ex) {
        String campos = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroDTO("DADOS_INVALIDOS", campos));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDTO> handleErroInesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroDTO("ERRO_INTERNO", "Ocorreu um erro inesperado"));
    }
}