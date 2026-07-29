package org.relatorio.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.relatorio.application.port.RelatorioRepositoryPort;
import org.relatorio.application.port.RelatoriosConsolidadosRepositoryPort;
import org.relatorio.config.DynamoDBProperties;
import org.relatorio.domain.exception.DynamoDBException;
import org.relatorio.domain.model.Relatorio;
import org.relatorio.domain.model.RelatorioDiario;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RelatoriosConsolidadosRepositoryAdapter implements RelatoriosConsolidadosRepositoryPort {
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDBProperties properties;
    private final RelatorioDiarioMapper mapper;

    @Override
    public Optional<RelatorioDiario> buscarPorData(String data) {
        try {
            log.debug("Consultando DynamoDB para data: {}", data);
            long startTime = System.currentTimeMillis();

            var table = enhancedClient.table(
                    properties.getTableNameConsolidado(),
                    TableSchema.fromBean(RelatorioDiarioDynamoDB.class)
            );

            var key = Key.builder()
                    .partitionValue(data)
                    .build();

            var item = table.getItem(r -> r.key(key));

            long duration = System.currentTimeMillis() - startTime;
            log.debug("Consulta ao DynamoDB concluída em {}ms", duration);

            return Optional.ofNullable(item)
                    .map(mapper::toDomain);

        } catch (DynamoDbException e) {
            log.error("Erro ao buscar relatório por data {}: {}", data, e.getMessage(), e);
            throw new DynamoDBException("Erro ao consultar DynamoDB", e);
        }
    }
}
