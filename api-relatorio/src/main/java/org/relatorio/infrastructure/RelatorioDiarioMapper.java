package org.relatorio.infrastructure;

import org.relatorio.domain.model.Relatorio;
import org.relatorio.domain.model.RelatorioDiario;
import org.springframework.stereotype.Component;

@Component
public class RelatorioDiarioMapper {
    public RelatorioDiario toDomain(RelatorioDiarioDynamoDB dynamoDB) {
        if (dynamoDB == null) {
            return null;
        }
        return RelatorioDiario.builder()
                .data(dynamoDB.getData())
                .totalAvaliacoes(dynamoDB.getTotalAvaliacoes())
                .somaNotas(dynamoDB.getSomaNotas())
                .mediaNotas(dynamoDB.getMediaNotas())
                .altaUrgencia(dynamoDB.getAltaUrgencia())
                .baixaUrgencia(dynamoDB.getBaixaUrgencia())
                .ultimaAtualizacao(dynamoDB.getUltimaAtualizacao())
                .build();
    }

    public RelatorioDiarioDynamoDB toDynamoDB(RelatorioDiario relatorio) {
        if (relatorio == null) {
            return null;
        }
        return RelatorioDiarioDynamoDB.builder()
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
