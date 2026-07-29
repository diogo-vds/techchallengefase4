package com.postech.techchallenge.fase4.avaliacao.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.postech.techchallenge.fase4.avaliacao.dto.AvaliacaoRequestRecord;
import com.postech.techchallenge.fase4.avaliacao.dto.AvaliacaoResponseRecord;
import com.postech.techchallenge.fase4.avaliacao.service.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponseRecord> receiveAvaliacao(@RequestBody AvaliacaoRequestRecord evento) throws JsonProcessingException {
        return ResponseEntity.ok(avaliacaoService.salvar(evento));
    }
}
