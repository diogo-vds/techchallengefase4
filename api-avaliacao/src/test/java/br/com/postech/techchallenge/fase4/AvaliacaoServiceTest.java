package br.com.postech.techchallenge.fase4;

import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import br.com.postech.techchallenge.fase4.model.Avaliacao;
import br.com.postech.techchallenge.fase4.model.Urgencia;
import br.com.postech.techchallenge.fase4.model.Perfil;
import br.com.postech.techchallenge.fase4.model.Usuario;
import br.com.postech.techchallenge.fase4.integration.QueuePublisher;
import br.com.postech.techchallenge.fase4.repository.AvaliacaoRepository;
import br.com.postech.techchallenge.fase4.service.AvaliacaoService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AvaliacaoServiceTest {

    private final InMemoryRepository repository = new InMemoryRepository();
    private final RecordingPublisher publisher = new RecordingPublisher();
    private final AvaliacaoService service = new AvaliacaoService(repository, publisher);
    private final Usuario estudante = estudante();

    @Test
    public void calcularUrgencia_deveRetornarAlta_paraNotasAte3() {
        assertEquals(Urgencia.ALTA, service.calcularUrgencia(0));
        assertEquals(Urgencia.ALTA, service.calcularUrgencia(3));
    }

    @Test
    public void calcularUrgencia_deveRetornarMedia_paraNotasEntre4e6() {
        assertEquals(Urgencia.MEDIA, service.calcularUrgencia(4));
        assertEquals(Urgencia.MEDIA, service.calcularUrgencia(6));
    }

    @Test
    public void calcularUrgencia_deveRetornarBaixa_paraNotasMaioresQue6() {
        assertEquals(Urgencia.BAIXA, service.calcularUrgencia(7));
        assertEquals(Urgencia.BAIXA, service.calcularUrgencia(10));
    }

    @Test
    public void salvar_devePersistirAntesDePublicar() {
        Avaliacao resultado = service.salvar(
                new AvaliacaoRequest("Atendimento demorado", 2), estudante);

        assertNotNull(resultado.getId());
        assertEquals(estudante.getId(), resultado.getEstudanteId());
        assertEquals(estudante.getEmail(), resultado.getEstudanteEmail());
        assertEquals(List.of("salvar", "publicar"), repository.eventos);
    }

    @Test
    public void salvar_deveMarcarErroQuandoSqsFalhar() {
        publisher.falhar = true;

        assertThrows(IllegalStateException.class,
                () -> service.salvar(new AvaliacaoRequest("Teste de falha", 5), estudante));
        assertEquals(List.of("salvar", "publicar"), repository.eventos);
    }

    private Usuario estudante() {
        Usuario usuario = new Usuario();
        usuario.setId("estudante-1");
        usuario.setNome("Estudante");
        usuario.setEmail("estudante@example.com");
        usuario.setPerfil(Perfil.ESTUDANTE);
        return usuario;
    }

    private class InMemoryRepository implements AvaliacaoRepository {
        private final List<String> eventos = new ArrayList<>();

        @Override
        public Avaliacao salvar(Avaliacao avaliacao) {
            eventos.add("salvar");
            return avaliacao;
        }

    }

    private class RecordingPublisher implements QueuePublisher {
        private boolean falhar;

        @Override
        public void publicar(Avaliacao avaliacao) {
            repository.eventos.add("publicar");
            if (falhar) {
                throw new IllegalStateException("SQS indisponivel");
            }
        }
    }

}
