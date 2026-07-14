package com.feedback.report.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class WeeklyReport {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Map<LocalDate, Long> avaliacoesPorDia;
    private Map<UrgencyLevel, Long> avaliacoesPorUrgencia;
    private double mediaGeral;
    private double mediaUrgentes;
    private long totalAvaliacoes;
    private long totalUrgentes;
}
