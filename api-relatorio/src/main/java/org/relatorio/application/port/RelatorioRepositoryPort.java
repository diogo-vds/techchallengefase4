package org.relatorio.application.port;

import org.relatorio.domain.model.Relatorio;

import java.util.List;
import java.util.Optional;

public interface RelatorioRepositoryPort {
    Optional<Relatorio> buscarPorId(Long id);
    List<Relatorio> buscarUltimos7Dias();
}
