package org.relatorio.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.relatorio.application.dto.RelatorioResponse;
import org.relatorio.application.usecase.BuscarRelatorioPorIdUseCase;
import org.relatorio.application.usecase.BuscarRelatoriosUltimos7DiasUseCase;
import org.relatorio.domain.exception.RelatorioNaoEncontradoException;
import org.relatorio.domain.model.Relatorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para RelatorioController")
class RelatorioControllerTest {

    @Mock
    private BuscarRelatorioPorIdUseCase buscarPorIdUseCase;

    @Mock
    private BuscarRelatoriosUltimos7DiasUseCase buscarUltimos7DiasUseCase;

    @Mock
    private RelatorioDTOMapper mapper;

    @InjectMocks
    private RelatorioController controller;

    private Relatorio relatorio;
    private RelatorioResponse relatorioResponse;
    private final Long ID_VALIDO = 1L;
    private final Long ID_INVALIDO = 999L;
    private LocalDateTime dataCadastro;

    @BeforeEach
    void setUp() {
        dataCadastro = LocalDateTime.now();

        // Cria um relatório para os testes
        relatorio = Relatorio.builder()
                .id(ID_VALIDO)
                .descricao("Relatório de vendas")
                .nota(8)
                .urgencia("ALTA")
                .dataCadastro(dataCadastro)
                .build();

        // Cria a resposta esperada
        relatorioResponse = RelatorioResponse.builder()
                .id(ID_VALIDO)
                .descricao("Relatório de vendas")
                .nota(8)
                .urgencia("ALTA")
                .dataCadastro(dataCadastro)
                .build();
    }

    // ==================== TESTES PARA buscarPorId ====================

    @Test
    @DisplayName("Deve retornar relatório com sucesso quando ID existe")
    void deveRetornarRelatorioComSucessoQuandoIdExiste() {
        // Arrange
        when(buscarPorIdUseCase.executar(ID_VALIDO)).thenReturn(relatorio);
        when(mapper.toResponse(relatorio)).thenReturn(relatorioResponse);

        // Act
        ResponseEntity<RelatorioResponse> response = controller.buscarPorId(ID_VALIDO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(ID_VALIDO);
        assertThat(response.getBody().getDescricao()).isEqualTo("Relatório de vendas");
        assertThat(response.getBody().getNota()).isEqualTo(8);
        assertThat(response.getBody().getUrgencia()).isEqualTo("ALTA");

        verify(buscarPorIdUseCase, times(1)).executar(ID_VALIDO);
        verify(mapper, times(1)).toResponse(relatorio);
        verifyNoMoreInteractions(buscarPorIdUseCase, mapper);
    }

    @Test
    @DisplayName("Deve lançar RelatorioNaoEncontradoException quando ID não existe")
    void deveLancarRelatorioNaoEncontradoExceptionQuandoIdNaoExiste() {
        // Arrange
        when(buscarPorIdUseCase.executar(ID_INVALIDO))
                .thenThrow(new RelatorioNaoEncontradoException(ID_INVALIDO));

        // Act & Assert
        assertThatThrownBy(() -> controller.buscarPorId(ID_INVALIDO))
                .isInstanceOf(RelatorioNaoEncontradoException.class)
                .hasMessageContaining(String.valueOf(ID_INVALIDO));

        verify(buscarPorIdUseCase, times(1)).executar(ID_INVALIDO);
        verify(mapper, never()).toResponse(any(Relatorio.class));
    }

    @Test
    @DisplayName("Deve retornar relatório com todos os campos preenchidos")
    void deveRetornarRelatorioComTodosCamposPreenchidos() {
        // Arrange
        Relatorio relatorioCompleto = Relatorio.builder()
                .id(2L)
                .descricao("Relatório completo")
                .nota(10)
                .urgencia("CRITICA")
                .dataCadastro(LocalDateTime.now())
                .build();

        RelatorioResponse responseCompleto = RelatorioResponse.builder()
                .id(2L)
                .descricao("Relatório completo")
                .nota(10)
                .urgencia("CRITICA")
                .dataCadastro(relatorioCompleto.getDataCadastro())
                .build();

        when(buscarPorIdUseCase.executar(2L)).thenReturn(relatorioCompleto);
        when(mapper.toResponse(relatorioCompleto)).thenReturn(responseCompleto);

        // Act
        ResponseEntity<RelatorioResponse> response = controller.buscarPorId(2L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getId()).isEqualTo(2L);
                    assertThat(r.getDescricao()).isEqualTo("Relatório completo");
                    assertThat(r.getNota()).isEqualTo(10);
                    assertThat(r.getUrgencia()).isEqualTo("CRITICA");
                    assertThat(r.getDataCadastro()).isEqualTo(relatorioCompleto.getDataCadastro());
                });

        verify(buscarPorIdUseCase, times(1)).executar(2L);
        verify(mapper, times(1)).toResponse(relatorioCompleto);
    }

    @Test
    @DisplayName("Deve retornar relatório com nota zero")
    void deveRetornarRelatorioComNotaZero() {
        // Arrange
        Relatorio relatorioNotaZero = Relatorio.builder()
                .id(3L)
                .descricao("Relatório nota zero")
                .nota(0)
                .urgencia("BAIXA")
                .dataCadastro(LocalDateTime.now())
                .build();

        RelatorioResponse responseNotaZero = RelatorioResponse.builder()
                .id(3L)
                .descricao("Relatório nota zero")
                .nota(0)
                .urgencia("BAIXA")
                .dataCadastro(relatorioNotaZero.getDataCadastro())
                .build();

        when(buscarPorIdUseCase.executar(3L)).thenReturn(relatorioNotaZero);
        when(mapper.toResponse(relatorioNotaZero)).thenReturn(responseNotaZero);

        // Act
        ResponseEntity<RelatorioResponse> response = controller.buscarPorId(3L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNota()).isZero();

        verify(buscarPorIdUseCase, times(1)).executar(3L);
        verify(mapper, times(1)).toResponse(relatorioNotaZero);
    }

    @Test
    @DisplayName("Deve retornar relatório com nota negativa")
    void deveRetornarRelatorioComNotaNegativa() {
        // Arrange
        Relatorio relatorioNotaNegativa = Relatorio.builder()
                .id(4L)
                .descricao("Relatório nota negativa")
                .nota(-1)
                .urgencia("BAIXA")
                .dataCadastro(LocalDateTime.now())
                .build();

        RelatorioResponse responseNotaNegativa = RelatorioResponse.builder()
                .id(4L)
                .descricao("Relatório nota negativa")
                .nota(-1)
                .urgencia("BAIXA")
                .dataCadastro(relatorioNotaNegativa.getDataCadastro())
                .build();

        when(buscarPorIdUseCase.executar(4L)).thenReturn(relatorioNotaNegativa);
        when(mapper.toResponse(relatorioNotaNegativa)).thenReturn(responseNotaNegativa);

        // Act
        ResponseEntity<RelatorioResponse> response = controller.buscarPorId(4L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNota()).isNegative();

        verify(buscarPorIdUseCase, times(1)).executar(4L);
        verify(mapper, times(1)).toResponse(relatorioNotaNegativa);
    }

    @Test
    @DisplayName("Deve retornar relatório com descrição vazia")
    void deveRetornarRelatorioComDescricaoVazia() {
        // Arrange
        Relatorio relatorioDescVazia = Relatorio.builder()
                .id(5L)
                .descricao("")
                .nota(5)
                .urgencia("MEDIA")
                .dataCadastro(LocalDateTime.now())
                .build();

        RelatorioResponse responseDescVazia = RelatorioResponse.builder()
                .id(5L)
                .descricao("")
                .nota(5)
                .urgencia("MEDIA")
                .dataCadastro(relatorioDescVazia.getDataCadastro())
                .build();

        when(buscarPorIdUseCase.executar(5L)).thenReturn(relatorioDescVazia);
        when(mapper.toResponse(relatorioDescVazia)).thenReturn(responseDescVazia);

        // Act
        ResponseEntity<RelatorioResponse> response = controller.buscarPorId(5L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getDescricao()).isEmpty();

        verify(buscarPorIdUseCase, times(1)).executar(5L);
        verify(mapper, times(1)).toResponse(relatorioDescVazia);
    }

    // ==================== TESTES PARA buscarUltimos7Dias ====================

    @Test
    @DisplayName("Deve retornar lista de relatórios dos últimos 7 dias com sucesso")
    void deveRetornarListaRelatoriosUltimos7DiasComSucesso() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        Relatorio relatorio1 = Relatorio.builder()
                .id(1L)
                .descricao("Relatório 1")
                .nota(8)
                .urgencia("ALTA")
                .dataCadastro(agora.minusDays(1))
                .build();

        Relatorio relatorio2 = Relatorio.builder()
                .id(2L)
                .descricao("Relatório 2")
                .nota(9)
                .urgencia("CRITICA")
                .dataCadastro(agora.minusDays(3))
                .build();

        Relatorio relatorio3 = Relatorio.builder()
                .id(3L)
                .descricao("Relatório 3")
                .nota(7)
                .urgencia("MEDIA")
                .dataCadastro(agora.minusDays(5))
                .build();

        List<Relatorio> relatorios = Arrays.asList(relatorio1, relatorio2, relatorio3);

        RelatorioResponse response1 = RelatorioResponse.builder()
                .id(1L).descricao("Relatório 1").nota(8).urgencia("ALTA").dataCadastro(agora.minusDays(1)).build();

        RelatorioResponse response2 = RelatorioResponse.builder()
                .id(2L).descricao("Relatório 2").nota(9).urgencia("CRITICA").dataCadastro(agora.minusDays(3)).build();

        RelatorioResponse response3 = RelatorioResponse.builder()
                .id(3L).descricao("Relatório 3").nota(7).urgencia("MEDIA").dataCadastro(agora.minusDays(5)).build();

        when(buscarUltimos7DiasUseCase.executar()).thenReturn(relatorios);
        when(mapper.toResponse(relatorio1)).thenReturn(response1);
        when(mapper.toResponse(relatorio2)).thenReturn(response2);
        when(mapper.toResponse(relatorio3)).thenReturn(response3);

        // Act
        ResponseEntity<List<RelatorioResponse>> response = controller.buscarUltimos7Dias();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody())
                .extracting(RelatorioResponse::getId)
                .containsExactly(1L, 2L, 3L);

        verify(buscarUltimos7DiasUseCase, times(1)).executar();
        verify(mapper, times(3)).toResponse(any(Relatorio.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há relatórios nos últimos 7 dias")
    void deveRetornarListaVaziaQuandoNaoHaRelatorios() {
        // Arrange
        when(buscarUltimos7DiasUseCase.executar()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<RelatorioResponse>> response = controller.buscarUltimos7Dias();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        verify(buscarUltimos7DiasUseCase, times(1)).executar();
        verify(mapper, never()).toResponse(any(Relatorio.class));
    }

    @Test
    @DisplayName("Deve retornar lista com um único relatório")
    void deveRetornarListaComUnicoRelatorio() {
        // Arrange
        List<Relatorio> relatorios = Collections.singletonList(relatorio);
        List<RelatorioResponse> responses = Collections.singletonList(relatorioResponse);

        when(buscarUltimos7DiasUseCase.executar()).thenReturn(relatorios);
        when(mapper.toResponse(relatorio)).thenReturn(relatorioResponse);

        // Act
        ResponseEntity<List<RelatorioResponse>> response = controller.buscarUltimos7Dias();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(ID_VALIDO);

        verify(buscarUltimos7DiasUseCase, times(1)).executar();
        verify(mapper, times(1)).toResponse(relatorio);
    }

    @Test
    @DisplayName("Deve retornar lista com relatórios de diferentes urgências")
    void deveRetornarListaComDiferentesUrgencias() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        Relatorio relatorioBaixa = Relatorio.builder()
                .id(4L).descricao("Baixa urgência").nota(6).urgencia("BAIXA").dataCadastro(agora.minusDays(1)).build();

        Relatorio relatorioMedia = Relatorio.builder()
                .id(5L).descricao("Média urgência").nota(7).urgencia("MEDIA").dataCadastro(agora.minusDays(3)).build();

        Relatorio relatorioAlta = Relatorio.builder()
                .id(6L).descricao("Alta urgência").nota(8).urgencia("ALTA").dataCadastro(agora.minusDays(5)).build();

        List<Relatorio> relatorios = Arrays.asList(relatorioBaixa, relatorioMedia, relatorioAlta);

        RelatorioResponse responseBaixa = RelatorioResponse.builder()
                .id(4L).descricao("Baixa urgência").nota(6).urgencia("BAIXA").dataCadastro(agora.minusDays(1)).build();

        RelatorioResponse responseMedia = RelatorioResponse.builder()
                .id(5L).descricao("Média urgência").nota(7).urgencia("MEDIA").dataCadastro(agora.minusDays(3)).build();

        RelatorioResponse responseAlta = RelatorioResponse.builder()
                .id(6L).descricao("Alta urgência").nota(8).urgencia("ALTA").dataCadastro(agora.minusDays(5)).build();

        when(buscarUltimos7DiasUseCase.executar()).thenReturn(relatorios);
        when(mapper.toResponse(relatorioBaixa)).thenReturn(responseBaixa);
        when(mapper.toResponse(relatorioMedia)).thenReturn(responseMedia);
        when(mapper.toResponse(relatorioAlta)).thenReturn(responseAlta);

        // Act
        ResponseEntity<List<RelatorioResponse>> response = controller.buscarUltimos7Dias();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .extracting(RelatorioResponse::getUrgencia)
                .containsExactly("BAIXA", "MEDIA", "ALTA");

        verify(buscarUltimos7DiasUseCase, times(1)).executar();
        verify(mapper, times(3)).toResponse(any(Relatorio.class));
    }

    @Test
    @DisplayName("Deve retornar lista com relatórios de diferentes notas")
    void deveRetornarListaComDiferentesNotas() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        Relatorio relatorioNota5 = Relatorio.builder()
                .id(7L).descricao("Nota 5").nota(5).urgencia("BAIXA").dataCadastro(agora.minusDays(1)).build();

        Relatorio relatorioNota8 = Relatorio.builder()
                .id(8L).descricao("Nota 8").nota(8).urgencia("MEDIA").dataCadastro(agora.minusDays(3)).build();

        Relatorio relatorioNota10 = Relatorio.builder()
                .id(9L).descricao("Nota 10").nota(10).urgencia("ALTA").dataCadastro(agora.minusDays(5)).build();

        List<Relatorio> relatorios = Arrays.asList(relatorioNota5, relatorioNota8, relatorioNota10);

        RelatorioResponse responseNota5 = RelatorioResponse.builder()
                .id(7L).descricao("Nota 5").nota(5).urgencia("BAIXA").dataCadastro(agora.minusDays(1)).build();

        RelatorioResponse responseNota8 = RelatorioResponse.builder()
                .id(8L).descricao("Nota 8").nota(8).urgencia("MEDIA").dataCadastro(agora.minusDays(3)).build();

        RelatorioResponse responseNota10 = RelatorioResponse.builder()
                .id(9L).descricao("Nota 10").nota(10).urgencia("ALTA").dataCadastro(agora.minusDays(5)).build();

        when(buscarUltimos7DiasUseCase.executar()).thenReturn(relatorios);
        when(mapper.toResponse(relatorioNota5)).thenReturn(responseNota5);
        when(mapper.toResponse(relatorioNota8)).thenReturn(responseNota8);
        when(mapper.toResponse(relatorioNota10)).thenReturn(responseNota10);

        // Act
        ResponseEntity<List<RelatorioResponse>> response = controller.buscarUltimos7Dias();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .extracting(RelatorioResponse::getNota)
                .containsExactly(5, 8, 10);

        verify(buscarUltimos7DiasUseCase, times(1)).executar();
        verify(mapper, times(3)).toResponse(any(Relatorio.class));
    }

    @Test
    @DisplayName("Deve retornar lista com relatórios ordenados por data")
    void deveRetornarListaOrdenadaPorData() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        Relatorio relatorioRecente = Relatorio.builder()
                .id(10L).descricao("Recente").nota(8).urgencia("ALTA").dataCadastro(agora.minusDays(1)).build();

        Relatorio relatorioIntermediario = Relatorio.builder()
                .id(11L).descricao("Intermediário").nota(7).urgencia("MEDIA").dataCadastro(agora.minusDays(3)).build();

        Relatorio relatorioAntigo = Relatorio.builder()
                .id(12L).descricao("Antigo").nota(6).urgencia("BAIXA").dataCadastro(agora.minusDays(6)).build();

        List<Relatorio> relatorios = Arrays.asList(relatorioRecente, relatorioIntermediario, relatorioAntigo);

        RelatorioResponse responseRecente = RelatorioResponse.builder()
                .id(10L).descricao("Recente").nota(8).urgencia("ALTA").dataCadastro(agora.minusDays(1)).build();

        RelatorioResponse responseIntermediario = RelatorioResponse.builder()
                .id(11L).descricao("Intermediário").nota(7).urgencia("MEDIA").dataCadastro(agora.minusDays(3)).build();

        RelatorioResponse responseAntigo = RelatorioResponse.builder()
                .id(12L).descricao("Antigo").nota(6).urgencia("BAIXA").dataCadastro(agora.minusDays(6)).build();

        when(buscarUltimos7DiasUseCase.executar()).thenReturn(relatorios);
        when(mapper.toResponse(relatorioRecente)).thenReturn(responseRecente);
        when(mapper.toResponse(relatorioIntermediario)).thenReturn(responseIntermediario);
        when(mapper.toResponse(relatorioAntigo)).thenReturn(responseAntigo);

        // Act
        ResponseEntity<List<RelatorioResponse>> response = controller.buscarUltimos7Dias();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .extracting(RelatorioResponse::getDataCadastro)
                .containsExactly(
                        relatorioRecente.getDataCadastro(),
                        relatorioIntermediario.getDataCadastro(),
                        relatorioAntigo.getDataCadastro()
                );

        verify(buscarUltimos7DiasUseCase, times(1)).executar();
        verify(mapper, times(3)).toResponse(any(Relatorio.class));
    }

    @Test
    @DisplayName("Deve retornar lista com muitos relatórios")
    void deveRetornarListaComMuitosRelatorios() {
        // Arrange
        List<Relatorio> muitosRelatorios = Arrays.asList(
                Relatorio.builder().id(1L).descricao("R1").nota(5).urgencia("BAIXA").dataCadastro(LocalDateTime.now().minusDays(1)).build(),
                Relatorio.builder().id(2L).descricao("R2").nota(6).urgencia("MEDIA").dataCadastro(LocalDateTime.now().minusDays(2)).build(),
                Relatorio.builder().id(3L).descricao("R3").nota(7).urgencia("ALTA").dataCadastro(LocalDateTime.now().minusDays(3)).build(),
                Relatorio.builder().id(4L).descricao("R4").nota(8).urgencia("CRITICA").dataCadastro(LocalDateTime.now().minusDays(4)).build(),
                Relatorio.builder().id(5L).descricao("R5").nota(9).urgencia("ALTA").dataCadastro(LocalDateTime.now().minusDays(5)).build()
        );

        when(buscarUltimos7DiasUseCase.executar()).thenReturn(muitosRelatorios);
        when(mapper.toResponse(any(Relatorio.class))).thenAnswer(invocation -> {
            Relatorio r = invocation.getArgument(0);
            return RelatorioResponse.builder()
                    .id(r.getId())
                    .descricao(r.getDescricao())
                    .nota(r.getNota())
                    .urgencia(r.getUrgencia())
                    .dataCadastro(r.getDataCadastro())
                    .build();
        });

        // Act
        ResponseEntity<List<RelatorioResponse>> response = controller.buscarUltimos7Dias();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(5);

        verify(buscarUltimos7DiasUseCase, times(1)).executar();
        verify(mapper, times(5)).toResponse(any(Relatorio.class));
    }




    @Test
    @DisplayName("Deve retornar status 200 OK para buscarPorId com sucesso")
    void deveRetornarStatus200OkParaBuscarPorId() {
        // Arrange
        when(buscarPorIdUseCase.executar(ID_VALIDO)).thenReturn(relatorio);
        when(mapper.toResponse(relatorio)).thenReturn(relatorioResponse);

        // Act
        ResponseEntity<RelatorioResponse> response = controller.buscarPorId(ID_VALIDO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Deve retornar status 200 OK para buscarUltimos7Dias com sucesso")
    void deveRetornarStatus200OkParaBuscarUltimos7Dias() {
        // Arrange
        when(buscarUltimos7DiasUseCase.executar()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<RelatorioResponse>> response = controller.buscarUltimos7Dias();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}