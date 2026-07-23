package org.relatorio.infrastructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.relatorio.application.dto.RelatorioResponse;
import org.relatorio.application.usecase.BuscarRelatorioPorIdUseCase;
import org.relatorio.application.usecase.BuscarRelatoriosUltimos7DiasUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "API para consulta de relatórios")
public class RelatorioController {
    private final BuscarRelatorioPorIdUseCase buscarPorIdUseCase;
    private final BuscarRelatoriosUltimos7DiasUseCase buscarUltimos7DiasUseCase;
    private final RelatorioDTOMapper mapper;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar relatório por ID",
            description = "Retorna um relatório específico baseado no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    public ResponseEntity<RelatorioResponse> buscarPorId(
            @Parameter(description = "ID do relatório", required = true, example = "1")
            @PathVariable @Positive Long id) {

        log.info("Recebida requisição para buscar relatório ID: {}", id);

        var relatorio = buscarPorIdUseCase.executar(id);

        log.debug("Relatório encontrado: ID {}", id);

        return ResponseEntity.ok(mapper.toResponse(relatorio));
    }

    @GetMapping
    @Operation(summary = "Buscar relatórios dos últimos 7 dias",
            description = "Retorna todos os relatórios cadastrados nos últimos 7 dias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatórios encontrados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar relatórios")
    })
    public ResponseEntity<List<RelatorioResponse>> buscarUltimos7Dias() {
        log.info("Recebida requisição para buscar relatórios dos últimos 7 dias");

        var relatorios = buscarUltimos7DiasUseCase.executar();

        var response = relatorios.stream()
                .map(mapper::toResponse)
                .toList();

        log.debug("Retornando {} relatórios", response.size());

        return ResponseEntity.ok(response);
    }
}
