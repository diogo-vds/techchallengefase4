package org.relatorio.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.relatorio.application.port.RelatorioRepositoryPort;
import org.relatorio.application.port.RelatoriosConsolidadosRepositoryPort;
import org.relatorio.domain.exception.RelatorioNaoEncontradoException;
import org.relatorio.domain.model.Relatorio;
import org.relatorio.domain.model.RelatorioDiario;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarRelatorioPorDataUseCase {
    private final RelatoriosConsolidadosRepositoryPort repository;

    public RelatorioDiario executar(String data) {
        log.debug("Buscando relatório por data: {}", data);
        return repository.buscarPorData(data)
                .orElseThrow(() -> {
                    log.warn("Relatório não encontrado com data: {}", data);
                    return new RelatorioNaoEncontradoException(data);
                });
    }
}