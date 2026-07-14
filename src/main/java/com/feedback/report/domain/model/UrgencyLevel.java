package com.feedback.report.domain.model;

public enum UrgencyLevel {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta"),
    CRITICA("Crítica");

    private final String descricao;

    UrgencyLevel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}