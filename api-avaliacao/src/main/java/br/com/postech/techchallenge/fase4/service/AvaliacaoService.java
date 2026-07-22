package br.com.postech.techchallenge.fase4.service;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.postech.techchallenge.fase4.integration.RabbitMqPublisher;
import br.com.postech.techchallenge.fase4.model.Avaliacao;
import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import br.com.postech.techchallenge.fase4.model.Urgencia;

public class AvaliacaoService {

    private final RabbitMqPublisher rabbitMqPublisher = new RabbitMqPublisher();

    public Avaliacao salvar(AvaliacaoRequest dto) {

        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setId(UUID.randomUUID().toString());
        avaliacao.setDescricao(dto.descricao());
        avaliacao.setNota(dto.nota());
        avaliacao.setUrgencia(calcularUrgencia(dto.nota()));
        avaliacao.setDataEnvio(LocalDateTime.now());

        try {
            // Publicar avaliação na fila RabbitMQ
            rabbitMqPublisher.publicarAvaliacao(avaliacao);
        } catch (Exception e) {
            System.err.println("Erro ao publicar na fila: " + e.getMessage());
            throw new RuntimeException("Erro ao salvar avaliação na fila RabbitMQ", e);
        }

        return avaliacao;
    }

    public Urgencia calcularUrgencia(Integer nota) {

        if (nota <= 3) {
            return Urgencia.ALTA;
        }

        if (nota <= 6) {
            return Urgencia.MEDIA;
        }

        return Urgencia.BAIXA;
    }
}