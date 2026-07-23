package com.postech.techchallenge.fase3.hospital.notificacao.dto;

public record Notificacaorecord(
    String destinatario,
    String assunto,
    String corpo
) {}
