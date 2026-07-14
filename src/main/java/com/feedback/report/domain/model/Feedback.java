package com.feedback.report.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Feedback {
    private String id;
    private String descricao;
    private int nota;
    private UrgencyLevel urgencia;
    private LocalDateTime dataEnvio;
    private String aulaId;

    public boolean isUrgent() {
        return UrgencyLevel.ALTA.equals(urgencia) ||
                UrgencyLevel.CRITICA.equals(urgencia);
    }
}
