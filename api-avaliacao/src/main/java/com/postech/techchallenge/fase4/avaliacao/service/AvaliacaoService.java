package com.postech.techchallenge.fase4.avaliacao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.techchallenge.fase4.avaliacao.aws.SnsPublisher;
import com.postech.techchallenge.fase4.avaliacao.dto.AvaliacaoEventoRecord;
import com.postech.techchallenge.fase4.avaliacao.dto.AvaliacaoRequestRecord;
import com.postech.techchallenge.fase4.avaliacao.dto.AvaliacaoResponseRecord;
import com.postech.techchallenge.fase4.avaliacao.dynamo.AvaliacaoDynamo;
import com.postech.techchallenge.fase4.avaliacao.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final SnsPublisher snsPublisher;
    private final ObjectMapper objectMapper;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, SnsPublisher snsPublisher, ObjectMapper objectMapper) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.snsPublisher = snsPublisher;
        this.objectMapper = objectMapper;
    }

    public AvaliacaoResponseRecord salvar(AvaliacaoRequestRecord avaliacaoRequestRecord) throws JsonProcessingException {
        AvaliacaoDynamo avaliacao = new AvaliacaoDynamo(avaliacaoRequestRecord.descricao(), avaliacaoRequestRecord.nota());
        avaliacaoRepository.salvar(avaliacao);
        AvaliacaoEventoRecord evento = new AvaliacaoEventoRecord(avaliacao.getId(),
                avaliacao.getDescricao(), avaliacao.getNota(), avaliacao.getUrgencia(), avaliacao.getDataCadastro());
        snsPublisher.publicar(objectMapper.writeValueAsString(evento));
        return new AvaliacaoResponseRecord("Avaliação salva com sucesso", avaliacao.getId());
    }

}
