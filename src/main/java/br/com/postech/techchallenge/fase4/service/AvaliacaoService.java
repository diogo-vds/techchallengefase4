package br.com.postech.techchallenge.fase4.service;

import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import br.com.postech.techchallenge.fase4.model.Urgencia;

public class AvaliacaoService {

    public Urgencia calcularUrgencia(Integer nota) {

        if (nota <= 3) {
            return Urgencia.ALTA;
        }

        if (nota <= 6) {
            return Urgencia.MEDIA;
        }

        return Urgencia.BAIXA;
    }

    public void salvar(AvaliacaoRequest dto) {
        //TODO: Implementar a lógica de salvar a avaliação no banco de dados
    }
}
