package br.com.postech.techchallenge.fase4.integration;

import br.com.postech.techchallenge.fase4.model.Avaliacao;

public interface QueuePublisher {

    void publicar(Avaliacao avaliacao);
}
