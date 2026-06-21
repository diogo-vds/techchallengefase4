package br.com.postech.techchallenge.fase4;

import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import br.com.postech.techchallenge.fase4.model.Urgencia;
import br.com.postech.techchallenge.fase4.service.AvaliacaoService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AvaliacaoServiceTest {

    private final AvaliacaoService service = new AvaliacaoService();

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
    public void salvar_naoDeveLancarException() {
        AvaliacaoRequest dto = new AvaliacaoRequest("descr", 5);
        assertDoesNotThrow(() -> service.salvar(dto));
    }
}
