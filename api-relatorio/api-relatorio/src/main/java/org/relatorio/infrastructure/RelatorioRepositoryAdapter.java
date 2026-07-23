package org.relatorio.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.relatorio.application.port.RelatorioRepositoryPort;
import org.relatorio.config.DynamoDBProperties;
import org.relatorio.domain.exception.DynamoDBException;
import org.relatorio.domain.model.Relatorio;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RelatorioRepositoryAdapter implements RelatorioRepositoryPort {
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDBProperties properties;
    private final RelatorioMapper mapper;

    @Override
    public Optional<Relatorio> buscarPorId(Long id) {
        try {
            log.debug("Consultando DynamoDB para ID: {}", id);
            long startTime = System.currentTimeMillis();

            var table = enhancedClient.table(
                    properties.getTableName(),
                    TableSchema.fromBean(RelatorioDynamoDB.class)
            );

            var key = Key.builder()
                    .partitionValue(id)
                    .build();

            var item = table.getItem(r -> r.key(key));

            long duration = System.currentTimeMillis() - startTime;
            log.debug("Consulta ao DynamoDB concluída em {}ms", duration);

            return Optional.ofNullable(item)
                    .map(mapper::toDomain);

        } catch (DynamoDbException e) {
            log.error("Erro ao buscar relatório por ID {}: {}", id, e.getMessage(), e);
            throw new DynamoDBException("Erro ao consultar DynamoDB", e);
        }
    }

    @Override
    public List<Relatorio> buscarUltimos7Dias() {
        try {
            log.debug("Consultando relatórios dos últimos 7 dias");
            long startTime = System.currentTimeMillis();

            var table = enhancedClient.table(
                    properties.getTableName(),
                    TableSchema.fromBean(RelatorioDynamoDB.class)
            );

            LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);

            // Implementação com Scan (filtro em memória)
            // Para produção, é recomendado usar GSI com índice por dataCadastro
            var scanRequest = ScanEnhancedRequest.builder()
                    .limit(1000)
                    .build();

            var items = table.scan(scanRequest)
                    .items()
                    .stream()
                    .filter(item -> item.getDataCadastro() != null &&
                            item.getDataCadastro().isAfter(dataLimite))
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            log.info("Consulta de últimos 7 dias retornou {} registros em {}ms",
                    items.size(), duration);

            return items;

        } catch (DynamoDbException e) {
            log.error("Erro ao buscar relatórios dos últimos 7 dias: {}", e.getMessage(), e);
            throw new DynamoDBException("Erro ao consultar DynamoDB", e);
        }
    }
}
