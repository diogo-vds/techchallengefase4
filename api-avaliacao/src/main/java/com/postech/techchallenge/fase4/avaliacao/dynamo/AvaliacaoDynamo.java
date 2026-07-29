package com.postech.techchallenge.fase4.avaliacao.dynamo;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDbBean
public class AvaliacaoDynamo {

    private String id;
    private String descricao;
    private Integer nota;
    private String urgencia;
    private LocalDateTime dataCadastro;

    public AvaliacaoDynamo() {
    }

    public AvaliacaoDynamo(String descricao, Integer nota) {
        this.id = UUID.randomUUID().toString();
        this.descricao = descricao;
        this.nota = nota;
        this.dataCadastro = LocalDateTime.now();
        this.urgencia = nota > 3 ? "Baixa" : "Alta";
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

    public void setUrgencia(String urgencia) {
        this.urgencia = urgencia;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getNota() {
        return nota;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
}
