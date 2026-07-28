package com.postech.techchallenge.fase3.hospital.notificacao.dto;

import java.time.LocalDateTime;

public record Notificacaorecord(
        Long id,
        String descricao,
        Integer nota,
        String urgencia,
        LocalDateTime dataCadastro
) {}
