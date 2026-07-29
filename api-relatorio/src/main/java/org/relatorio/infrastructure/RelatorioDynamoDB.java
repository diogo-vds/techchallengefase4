package org.relatorio.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class RelatorioDynamoDB {
    private Long id;
    private String descricao;
    private Integer nota;
    private String urgencia;
    private LocalDateTime dataCadastro;

    @DynamoDbPartitionKey
    public Long getId() {
        return id;
    }
}
