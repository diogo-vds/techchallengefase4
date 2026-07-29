package com.postech.techchallenge.fase4.avaliacao.dto;

import java.time.LocalDateTime;

public record AvaliacaoEventoRecord (
        String id,
        String descricao,
        Integer nota,
        String urgencia,
        LocalDateTime dataCadastro
){
}
