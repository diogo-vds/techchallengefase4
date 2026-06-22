package br.com.postech.techchallenge.fase4.service;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.postech.techchallenge.fase4.model.Avaliacao;
import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import br.com.postech.techchallenge.fase4.model.Urgencia;
import br.com.postech.techchallenge.fase4.repository.AvaliacaoRepository;
import br.com.postech.techchallenge.fase4.repository.JsonAvaliacaoRepository;

public class AvaliacaoService {

    private final AvaliacaoRepository repository =
            new JsonAvaliacaoRepository();

    public Avaliacao salvar(AvaliacaoRequest dto) {

        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setId(UUID.randomUUID().toString());
        avaliacao.setDescricao(dto.descricao());
        avaliacao.setNota(dto.nota());
        avaliacao.setUrgencia(calcularUrgencia(dto.nota()));
        avaliacao.setDataEnvio(LocalDateTime.now());

        return repository.salvar(avaliacao);
    }

    private Urgencia calcularUrgencia(Integer nota) {

        if (nota <= 3) {
            return Urgencia.ALTA;
        }

        if (nota <= 6) {
            return Urgencia.MEDIA;
        }

        return Urgencia.BAIXA;
    }
}