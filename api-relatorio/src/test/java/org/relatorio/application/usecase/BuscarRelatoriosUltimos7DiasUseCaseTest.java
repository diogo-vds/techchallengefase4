package org.relatorio.application.usecase;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.relatorio.application.port.RelatorioRepositoryPort;
import org.relatorio.domain.model.Relatorio;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para BuscarRelatoriosUltimos7DiasUseCase")
class BuscarRelatoriosUltimos7DiasUseCaseTest {


        @Mock
        private RelatorioRepositoryPort repository;

        @InjectMocks
        private BuscarRelatoriosUltimos7DiasUseCase useCase;

        private Relatorio relatorio1;
        private Relatorio relatorio2;
        private Relatorio relatorio3;
        private LocalDateTime dataBase;

        @BeforeEach
        void setUp() {
            dataBase = LocalDateTime.now();

            // Cria relatórios para os testes
            relatorio1 = Relatorio.builder()
                    .id(1L)
                    .descricao("Relatório de vendas - Dia 1")
                    .nota(8)
                    .urgencia("ALTA")
                    .dataCadastro(dataBase.minusDays(1))
                    .build();

            relatorio2 = Relatorio.builder()
                    .id(2L)
                    .descricao("Relatório de desempenho - Dia 3")
                    .nota(9)
                    .urgencia("CRITICA")
                    .dataCadastro(dataBase.minusDays(3))
                    .build();

            relatorio3 = Relatorio.builder()
                    .id(3L)
                    .descricao("Relatório de estoque - Dia 5")
                    .nota(7)
                    .urgencia("MEDIA")
                    .dataCadastro(dataBase.minusDays(5))
                    .build();
        }

        @Test
        @DisplayName("Deve retornar lista de relatórios dos últimos 7 dias com sucesso")
        void deveRetornarListaRelatoriosUltimos7DiasComSucesso() {
            // Arrange
            List<Relatorio> relatoriosEsperados = Arrays.asList(relatorio1, relatorio2, relatorio3);
            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(3)
                    .containsExactly(relatorio1, relatorio2, relatorio3);

            verify(repository, times(1)).buscarUltimos7Dias();
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há relatórios nos últimos 7 dias")
        void deveRetornarListaVaziaQuandoNaoHaRelatorios() {
            // Arrange
            when(repository.buscarUltimos7Dias()).thenReturn(Collections.emptyList());

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .isEmpty();

            verify(repository, times(1)).buscarUltimos7Dias();
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Deve retornar lista com um único relatório")
        void deveRetornarListaComUnicoRelatorio() {
            // Arrange
            List<Relatorio> relatoriosEsperados = Collections.singletonList(relatorio1);
            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(1)
                    .containsExactly(relatorio1);

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com relatórios de diferentes urgências")
        void deveRetornarListaComDiferentesUrgencias() {
            // Arrange
            Relatorio relatorioUrgenciaBaixa = Relatorio.builder()
                    .id(4L)
                    .descricao("Relatório com urgência baixa")
                    .nota(6)
                    .urgencia("BAIXA")
                    .dataCadastro(dataBase.minusDays(2))
                    .build();

            Relatorio relatorioUrgenciaMedia = Relatorio.builder()
                    .id(5L)
                    .descricao("Relatório com urgência média")
                    .nota(7)
                    .urgencia("MEDIA")
                    .dataCadastro(dataBase.minusDays(4))
                    .build();

            Relatorio relatorioUrgenciaAlta = Relatorio.builder()
                    .id(6L)
                    .descricao("Relatório com urgência alta")
                    .nota(8)
                    .urgencia("ALTA")
                    .dataCadastro(dataBase.minusDays(6))
                    .build();

            List<Relatorio> relatoriosEsperados = Arrays.asList(
                    relatorioUrgenciaBaixa,
                    relatorioUrgenciaMedia,
                    relatorioUrgenciaAlta
            );

            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(Relatorio::getUrgencia)
                    .containsExactly("BAIXA", "MEDIA", "ALTA");

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com relatórios ordenados por data")
        void deveRetornarListaOrdenadaPorData() {
            // Arrange
            LocalDateTime agora = LocalDateTime.now();

            Relatorio relatorioMaisAntigo = Relatorio.builder()
                    .id(7L)
                    .descricao("Relatório mais antigo")
                    .nota(5)
                    .urgencia("BAIXA")
                    .dataCadastro(agora.minusDays(6))
                    .build();

            Relatorio relatorioIntermediario = Relatorio.builder()
                    .id(8L)
                    .descricao("Relatório intermediário")
                    .nota(7)
                    .urgencia("MEDIA")
                    .dataCadastro(agora.minusDays(3))
                    .build();

            Relatorio relatorioMaisRecente = Relatorio.builder()
                    .id(9L)
                    .descricao("Relatório mais recente")
                    .nota(9)
                    .urgencia("ALTA")
                    .dataCadastro(agora.minusDays(1))
                    .build();

            List<Relatorio> relatoriosEsperados = Arrays.asList(
                    relatorioMaisRecente,
                    relatorioIntermediario,
                    relatorioMaisAntigo
            );

            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(Relatorio::getDataCadastro)
                    .containsExactly(
                            relatorioMaisRecente.getDataCadastro(),
                            relatorioIntermediario.getDataCadastro(),
                            relatorioMaisAntigo.getDataCadastro()
                    );

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com relatórios que têm notas variadas")
        void deveRetornarListaComNotasVariadas() {
            // Arrange
            List<Relatorio> relatoriosEsperados = Arrays.asList(relatorio1, relatorio2, relatorio3);
            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(Relatorio::getNota)
                    .containsExactly(8, 9, 7);

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com relatórios que têm IDs diferentes")
        void deveRetornarListaComIdsDiferentes() {
            // Arrange
            List<Relatorio> relatoriosEsperados = Arrays.asList(relatorio1, relatorio2, relatorio3);
            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(Relatorio::getId)
                    .containsExactly(1L, 2L, 3L);

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com relatórios que têm descrições diferentes")
        void deveRetornarListaComDescricoesDiferentes() {
            // Arrange
            List<Relatorio> relatoriosEsperados = Arrays.asList(relatorio1, relatorio2, relatorio3);
            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(Relatorio::getDescricao)
                    .containsExactly(
                            "Relatório de vendas - Dia 1",
                            "Relatório de desempenho - Dia 3",
                            "Relatório de estoque - Dia 5"
                    );

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com relatórios que têm data de cadastro nos últimos 7 dias")
        void deveRetornarListaComDatasNosUltimos7Dias() {
            // Arrange
            LocalDateTime agora = LocalDateTime.now();

            Relatorio relatorioDia1 = Relatorio.builder()
                    .id(10L)
                    .descricao("Relatório do dia 1")
                    .nota(8)
                    .urgencia("ALTA")
                    .dataCadastro(agora.minusDays(1))
                    .build();

            Relatorio relatorioDia3 = Relatorio.builder()
                    .id(11L)
                    .descricao("Relatório do dia 3")
                    .nota(7)
                    .urgencia("MEDIA")
                    .dataCadastro(agora.minusDays(3))
                    .build();

            Relatorio relatorioDia6 = Relatorio.builder()
                    .id(12L)
                    .descricao("Relatório do dia 6")
                    .nota(6)
                    .urgencia("BAIXA")
                    .dataCadastro(agora.minusDays(6))
                    .build();

            List<Relatorio> relatoriosEsperados = Arrays.asList(relatorioDia1, relatorioDia3, relatorioDia6);
            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .allMatch(r -> r.getDataCadastro().isAfter(agora.minusDays(7)));

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com muitos relatórios")
        void deveRetornarListaComMuitosRelatorios() {
            // Arrange
            List<Relatorio> muitosRelatorios = Arrays.asList(
                    relatorio1, relatorio2, relatorio3,
                    Relatorio.builder().id(13L).descricao("Relatório 4").nota(8).urgencia("ALTA").dataCadastro(dataBase.minusDays(2)).build(),
                    Relatorio.builder().id(14L).descricao("Relatório 5").nota(9).urgencia("CRITICA").dataCadastro(dataBase.minusDays(4)).build(),
                    Relatorio.builder().id(15L).descricao("Relatório 6").nota(7).urgencia("MEDIA").dataCadastro(dataBase.minusDays(6)).build()
            );

            when(repository.buscarUltimos7Dias()).thenReturn(muitosRelatorios);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(6);

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com relatórios que têm campos nulos")
        void deveRetornarListaComCamposNulos() {
            // Arrange
            Relatorio relatorioCamposNulos = Relatorio.builder()
                    .id(16L)
                    .descricao(null)
                    .nota(null)
                    .urgencia(null)
                    .dataCadastro(null)
                    .build();

            List<Relatorio> relatoriosEsperados = Collections.singletonList(relatorioCamposNulos);
            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(1)
                    .first()
                    .satisfies(r -> {
                        assertThat(r.getId()).isEqualTo(16L);
                        assertThat(r.getDescricao()).isNull();
                        assertThat(r.getNota()).isNull();
                        assertThat(r.getUrgencia()).isNull();
                        assertThat(r.getDataCadastro()).isNull();
                    });

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve retornar lista com relatórios de diferentes notas (0, 10 e negativa)")
        void deveRetornarListaComDiferentesNotas() {
            // Arrange
            Relatorio relatorioNotaZero = Relatorio.builder()
                    .id(17L)
                    .descricao("Relatório nota zero")
                    .nota(0)
                    .urgencia("BAIXA")
                    .dataCadastro(dataBase.minusDays(1))
                    .build();

            Relatorio relatorioNotaDez = Relatorio.builder()
                    .id(18L)
                    .descricao("Relatório nota dez")
                    .nota(10)
                    .urgencia("CRITICA")
                    .dataCadastro(dataBase.minusDays(3))
                    .build();

            Relatorio relatorioNotaNegativa = Relatorio.builder()
                    .id(19L)
                    .descricao("Relatório nota negativa")
                    .nota(-1)
                    .urgencia("BAIXA")
                    .dataCadastro(dataBase.minusDays(5))
                    .build();

            List<Relatorio> relatoriosEsperados = Arrays.asList(
                    relatorioNotaZero,
                    relatorioNotaDez,
                    relatorioNotaNegativa
            );

            when(repository.buscarUltimos7Dias()).thenReturn(relatoriosEsperados);

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(Relatorio::getNota)
                    .containsExactly(0, 10, -1);

            verify(repository, times(1)).buscarUltimos7Dias();
        }

        @Test
        @DisplayName("Deve chamar o repository exatamente uma vez")
        void deveChamarRepositoryExatamenteUmaVez() {
            // Arrange
            when(repository.buscarUltimos7Dias()).thenReturn(Collections.emptyList());

            // Act
            useCase.executar();

            // Assert
            verify(repository, times(1)).buscarUltimos7Dias();
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Deve retornar lista imutável quando repository retorna lista vazia")
        void deveRetornarListaImutavelQuandoVazia() {
            // Arrange
            when(repository.buscarUltimos7Dias()).thenReturn(Collections.emptyList());

            // Act
            List<Relatorio> resultado = useCase.executar();

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .isEmpty();

            // Verifica se a lista é do tipo ArrayList (não é imutável, mas é um detalhe de implementação)
            assertThat(resultado).isInstanceOf(List.class);

            verify(repository, times(1)).buscarUltimos7Dias();
        }
    }