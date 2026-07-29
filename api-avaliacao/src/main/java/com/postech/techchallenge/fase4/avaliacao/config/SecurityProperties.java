package com.postech.techchallenge.fase4.avaliacao.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    @NotBlank(message = "Admin username é obrigatório")
    private String adminUsername = "admin";

    @NotBlank(message = "Admin password é obrigatório")
    private String adminPassword = "admin123";

    @NotBlank(message = "User username é obrigatório")
    private String userUsername = "user";

    @NotBlank(message = "User password é obrigatório")
    private String userPassword = "user123";
}
