package org.relatorio.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDiarioResponse {
    private String data;
    private Integer totalAvaliacoes;
    private Integer somaNotas;
    private Double mediaNotas;
    private Integer altaUrgencia;
    private Integer baixaUrgencia;
    private LocalDateTime ultimaAtualizacao;;
}
