package org.relatorio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI relatoriosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Relatórios Service API")
                        .description("API para consulta de relatórios armazenados no DynamoDB")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Relatórios Team")
                                .email("suporte@relatorios.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
