package org.relatorio.infrastructure;

import org.relatorio.application.dto.RelatorioResponse;
import org.relatorio.domain.model.Relatorio;
import org.springframework.stereotype.Component;

@Component
public class RelatorioDTOMapper {
    public RelatorioResponse toResponse(Relatorio relatorio) {
        if (relatorio == null) {
            return null;
        }
        return RelatorioResponse.builder()
                .id(relatorio.getId())
                .descricao(relatorio.getDescricao())
                .nota(relatorio.getNota())
                .urgencia(relatorio.getUrgencia())
                .dataCadastro(relatorio.getDataCadastro())
                .build();
    }
}
