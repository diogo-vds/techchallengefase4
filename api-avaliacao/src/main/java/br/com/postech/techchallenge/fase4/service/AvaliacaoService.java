package br.com.postech.techchallenge.fase4.service;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.postech.techchallenge.fase4.integration.QueuePublisher;
import br.com.postech.techchallenge.fase4.integration.SqsPublisher;
import br.com.postech.techchallenge.fase4.model.Avaliacao;
import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import br.com.postech.techchallenge.fase4.model.Urgencia;
import br.com.postech.techchallenge.fase4.repository.AvaliacaoRepository;
import br.com.postech.techchallenge.fase4.repository.DynamoDbAvaliacaoRepository;

public class AvaliacaoService {

    private final AvaliacaoRepository repository;
    private final QueuePublisher queuePublisher;

    public AvaliacaoService() {
        this(new DynamoDbAvaliacaoRepository(), new SqsPublisher());
    }

    public AvaliacaoService(AvaliacaoRepository repository, QueuePublisher queuePublisher) {
        this.repository = repository;
        this.queuePublisher = queuePublisher;
    }

    public Avaliacao salvar(AvaliacaoRequest dto) {

        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setId(UUID.randomUUID().toString());
        avaliacao.setDescricao(dto.descricao());
        avaliacao.setNota(dto.nota());
        avaliacao.setUrgencia(calcularUrgencia(dto.nota()));
        avaliacao.setDataEnvio(LocalDateTime.now());
        repository.salvar(avaliacao);

        try {
            queuePublisher.publicar(avaliacao);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Avaliacao persistida, mas nao publicada na SQS", e);
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
