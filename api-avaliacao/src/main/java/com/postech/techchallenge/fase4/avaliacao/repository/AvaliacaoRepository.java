package com.postech.techchallenge.fase4.avaliacao.repository;

import com.postech.techchallenge.fase4.avaliacao.dynamo.AvaliacaoDynamo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class AvaliacaoRepository{
    private final DynamoDbTable<AvaliacaoDynamo> tabela;

    public AvaliacaoRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.tabela-avaliacoes}") String nomeTabela) {

        this.tabela = enhancedClient.table(
                nomeTabela,
                TableSchema.fromBean(AvaliacaoDynamo.class)
        );
    }

    public void salvar(AvaliacaoDynamo avaliacao) {
        tabela.putItem(avaliacao);
    }

    public AvaliacaoDynamo buscar(Long id) {
        return tabela.getItem(Key.builder().partitionValue(id).build());
    }
}
