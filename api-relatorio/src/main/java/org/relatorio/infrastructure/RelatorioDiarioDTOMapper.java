package org.relatorio.infrastructure;

import org.relatorio.application.dto.RelatorioDiarioResponse;
import org.relatorio.application.dto.RelatorioResponse;
import org.relatorio.domain.model.Relatorio;
import org.relatorio.domain.model.RelatorioDiario;
import org.springframework.stereotype.Component;

@Component
public class RelatorioDiarioDTOMapper {

    public RelatorioDiarioResponse toResponse(RelatorioDiario relatorio) {
        if (relatorio == null) {
            return null;
        }
        return RelatorioDiarioResponse.builder()
                .data(relatorio.getData())
                .totalAvaliacoes(relatorio.getTotalAvaliacoes())
                .somaNotas(relatorio.getSomaNotas())
                .mediaNotas(relatorio.getMediaNotas())
                .altaUrgencia(relatorio.getAltaUrgencia())
                .baixaUrgencia(relatorio.getBaixaUrgencia())
                .ultimaAtualizacao(relatorio.getUltimaAtualizacao())
                .build();
    }
}

