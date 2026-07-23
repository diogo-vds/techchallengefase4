package org.relatorio.infrastructure;

import org.relatorio.domain.model.Relatorio;
import org.springframework.stereotype.Component;

@Component
public class RelatorioMapper {
    public Relatorio toDomain(RelatorioDynamoDB dynamoDB) {
        if (dynamoDB == null) {
            return null;
        }
        return Relatorio.builder()
                .id(dynamoDB.getId())
                .descricao(dynamoDB.getDescricao())
                .nota(dynamoDB.getNota())
                .urgencia(dynamoDB.getUrgencia())
                .dataCadastro(dynamoDB.getDataCadastro())
                .build();
    }

    public RelatorioDynamoDB toDynamoDB(Relatorio relatorio) {
        if (relatorio == null) {
            return null;
        }
        return RelatorioDynamoDB.builder()
                .id(relatorio.getId())
                .descricao(relatorio.getDescricao())
                .nota(relatorio.getNota())
                .urgencia(relatorio.getUrgencia())
                .dataCadastro(relatorio.getDataCadastro())
                .build();
    }
}
