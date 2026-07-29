package org.relatorio.infrastructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.relatorio.application.dto.RelatorioDiarioResponse;
import org.relatorio.application.dto.RelatorioResponse;
import org.relatorio.application.usecase.BuscarRelatorioPorDataUseCase;
import org.relatorio.application.usecase.BuscarRelatorioPorIdUseCase;
import org.relatorio.application.usecase.BuscarRelatorioSemanalUseCase;
import org.relatorio.application.usecase.BuscarRelatoriosUltimos7DiasUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final BuscarRelatorioPorDataUseCase buscarPorDataUseCase;
    private final BuscarRelatorioSemanalUseCase buscarPorSemanaUseCase;
    private final BuscarRelatoriosUltimos7DiasUseCase buscarUltimos7DiasUseCase;
    private final RelatorioDTOMapper mapper;
    private final RelatorioDiarioDTOMapper mapperDiario;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Buscar relatório por ID",
            description = "Retorna um relatório específico baseado no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado - Credenciais inválidas"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Permissão insuficiente"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    public ResponseEntity<RelatorioResponse> buscarPorId(
            @Parameter(description = "ID do relatório", required = true, example = "1")
            @PathVariable @Positive Long id) {

        log.info("📥 Recebida requisição para buscar relatório ID: {} por usuário: {}",
                id, getCurrentUsername());

        var relatorio = buscarPorIdUseCase.executar(id);

        log.debug("✅ Relatório encontrado: ID {}", id);

        return ResponseEntity.ok(mapper.toResponse(relatorio));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Buscar relatórios dos últimos 7 dias",
            description = "Retorna todos os relatórios cadastrados nos últimos 7 dias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatórios encontrados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado - Credenciais inválidas"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Permissão insuficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar relatórios")
    })
    public ResponseEntity<List<RelatorioResponse>> buscarUltimos7Dias() {
        log.info("📋 Recebida requisição para buscar relatórios dos últimos 7 dias por usuário: {}",
                getCurrentUsername());

        var relatorios = buscarUltimos7DiasUseCase.executar();

        var response = relatorios.stream()
                .map(mapper::toResponse)
                .toList();

        log.debug("✅ Retornando {} relatórios", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/diario/{data}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Buscar relatório por data",
            description = "Retorna um relatório específico baseado na data fornecida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado - Credenciais inválidas"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Permissão insuficiente"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    public ResponseEntity<RelatorioDiarioResponse> buscarPorData(
            @Parameter(description = "Data do relatório", required = true, example = "2024-06-01")
            @PathVariable String data) {

        log.info("📥 Recebida requisição para buscar relatório data: {} por usuário: {}",
                data, getCurrentUsername());

        var relatorio = buscarPorDataUseCase.executar(data);

        log.debug("✅ Relatório encontrado: data {}", data);

        return ResponseEntity.ok(mapperDiario.toResponse(relatorio));
    }

    @GetMapping("/semanal/{data}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Buscar relatório por semana",
            description = "Retorna um relatório específico baseado na semana fornecida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado - Credenciais inválidas"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Permissão insuficiente"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    public ResponseEntity<RelatorioDiarioResponse> buscarPorSemana(
            @Parameter(description = "Data do relatório", required = true, example = "2024-06-01")
            @PathVariable String data) {

        log.info("📥 Recebida requisição para buscar relatório semanal data: {} por usuário: {}",
                data, getCurrentUsername());

        var relatorio = buscarPorSemanaUseCase.executar(data);

        log.debug("✅ Relatório semanal encontrado: data {}", data);

        return ResponseEntity.ok(mapperDiario.toResponse(relatorio));
    }


    /**
     * Obtém o nome do usuário autenticado
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return "anonymous";
    }
}