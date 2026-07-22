package br.com.postech.techchallenge.fase4.model;

import java.time.LocalDateTime;

public class Avaliacao {

    private String id;
    private String descricao;
    private Integer nota;
    private Urgencia urgencia;
    private LocalDateTime dataEnvio;

    public Avaliacao() {
    }

    public Avaliacao(
            String id,
            String descricao,
            Integer nota,
            Urgencia urgencia,
            LocalDateTime dataEnvio) {

        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = urgencia;
        this.dataEnvio = dataEnvio;
    }

    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getNota() {
        return nota;
    }

    public Urgencia getUrgencia() {
        return urgencia;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public void setUrgencia(Urgencia urgencia) {
        this.urgencia = urgencia;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}