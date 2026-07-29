package org.relatorio.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.relatorio.application.port.RelatorioRepositoryPort;
import org.relatorio.domain.model.Relatorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarRelatoriosUltimos7DiasUseCase {
    private final RelatorioRepositoryPort repository;

    public List<Relatorio> executar() {
        log.debug("Buscando relatórios dos últimos 7 dias");
        List<Relatorio> relatorios = repository.buscarUltimos7Dias();
        log.info("Encontrados {} relatórios dos últimos 7 dias", relatorios.size());
        return relatorios;
    }
}
