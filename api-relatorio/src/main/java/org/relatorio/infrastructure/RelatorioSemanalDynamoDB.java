package org.relatorio.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class RelatorioSemanalDynamoDB {
    private String data;
    private Integer totalAvaliacoes;
    private Integer somaNotas;
    private Double mediaNotas;
    private Integer altaUrgencia;
    private Integer baixaUrgencia;
    private LocalDateTime ultimaAtualizacao;

    @DynamoDbPartitionKey
    public String getData() {
        return data;
    }
}
