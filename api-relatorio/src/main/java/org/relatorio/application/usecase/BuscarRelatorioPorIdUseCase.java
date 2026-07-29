package org.relatorio.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.relatorio.application.port.RelatorioRepositoryPort;
import org.relatorio.domain.exception.RelatorioNaoEncontradoException;
import org.relatorio.domain.model.Relatorio;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarRelatorioPorIdUseCase {
    private final RelatorioRepositoryPort repository;

    public Relatorio executar(Long id) {
        log.debug("Buscando relatório por ID: {}", id);
        return repository.buscarPorId(id)
                .orElseThrow(() -> {
                    log.warn("Relatório não encontrado com ID: {}", id);
                    return new RelatorioNaoEncontradoException(id);
                });
    }
}