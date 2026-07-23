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
public class RelatorioResponse {
    private Long id;
    private String descricao;
    private Integer nota;
    private String urgencia;
    private LocalDateTime dataCadastro;
}
