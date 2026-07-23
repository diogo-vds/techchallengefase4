package org.relatorio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws.dynamodb")
public class DynamoDBProperties {
    private String tableName;
    private String endpoint;
}
