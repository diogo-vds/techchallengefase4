package br.com.postech.techchallenge.fase4;

import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AvaliacaoRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void serializacaoDeserializacao_deveFuncionar() throws Exception {
        AvaliacaoRequest original = new AvaliacaoRequest("teste", 8);
        String json = mapper.writeValueAsString(original);

        AvaliacaoRequest parsed = mapper.readValue(json, AvaliacaoRequest.class);

        assertEquals(original.descricao(), parsed.descricao());
        assertEquals(original.nota(), parsed.nota());
    }
}
