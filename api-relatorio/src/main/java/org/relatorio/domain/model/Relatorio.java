package org.relatorio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relatorio {
    private Long id;
    private String descricao;
    private Integer nota;
    private String urgencia;
    private LocalDateTime dataCadastro;
}