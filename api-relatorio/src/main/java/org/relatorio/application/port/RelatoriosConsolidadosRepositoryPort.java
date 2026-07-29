package org.relatorio.application.port;

import org.relatorio.domain.model.Relatorio;
import org.relatorio.domain.model.RelatorioDiario;

import java.util.List;
import java.util.Optional;

public interface RelatoriosConsolidadosRepositoryPort {
    Optional<RelatorioDiario> buscarPorData(String data);
}
