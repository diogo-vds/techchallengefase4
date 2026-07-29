package org.relatorio.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.relatorio.application.port.RelatoriosConsolidadosRepositoryPort;
import org.relatorio.domain.exception.RelatorioNaoEncontradoException;
import org.relatorio.domain.model.RelatorioDiario;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarRelatorioSemanalUseCase {
    private final RelatoriosConsolidadosRepositoryPort repository;

    public RelatorioDiario executar(String data) {
        log.debug("Buscando relatório por data: {}", data);
        RelatorioDiario relatorio = repository.buscarPorData(getSemana(data))
                .orElseThrow(() -> {
                    log.warn("Relatório não encontrado com data: {}", data);
                    return new RelatorioNaoEncontradoException(data);
                });
        relatorio.setData(getUltimoDiaSemana(relatorio.getData()).toString());
        return relatorio;
    }

    public static String getSemana(String dataStr) {
        LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ISO_LOCAL_DATE);
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int semana = data.get(wf.weekOfYear());
        return data.getYear() + "-S" + semana;
    }

    public LocalDateTime getUltimoDiaSemana(String semanaStr) {
        String[] partes = semanaStr.split("-S");
        int ano = Integer.parseInt(partes[0]);
        int semana = Integer.parseInt(partes[1]);
        WeekFields wf = WeekFields.of(Locale.getDefault());
        LocalDate primeiroDiaSemana = LocalDate.ofYearDay(ano, 1)
                .with(wf.weekOfYear(), semana)
                .with(wf.dayOfWeek(), 1);
        LocalDate ultimoDiaSemana = primeiroDiaSemana.with(DayOfWeek.SUNDAY);
        return ultimoDiaSemana.atStartOfDay();
    }
}