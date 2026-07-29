package org.relatorio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDiario {
    private String data;
    private Integer totalAvaliacoes;
    private Integer somaNotas;
    private Double mediaNotas;
    private Integer altaUrgencia;
    private Integer baixaUrgencia;
    private LocalDateTime ultimaAtualizacao;

}