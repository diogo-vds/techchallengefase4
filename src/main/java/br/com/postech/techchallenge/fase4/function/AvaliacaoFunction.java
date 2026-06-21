package br.com.postech.techchallenge.fase4.function;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import br.com.postech.techchallenge.fase4.service.AvaliacaoService;

public class AvaliacaoFunction {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final AvaliacaoService avaliacaoService = new AvaliacaoService();

    @FunctionName("avaliacao")
    public HttpResponseMessage execute(

            @HttpTrigger(
                    name = "request",
                    methods = { HttpMethod.POST },
                    authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<Optional<String>> request,

            final ExecutionContext context) {

        return run(request, context);
    }

    public HttpResponseMessage run(
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {

            context.getLogger().info("Recebendo nova avaliação");

            Optional<String> maybeBody = request.getBody();

            if (maybeBody.isEmpty()) {
                return request
                        .createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json")
                        .body(Map.of(
                                "erro", "Body da requisição é obrigatório"))
                        .build();
            }

            AvaliacaoRequest dto =
                    objectMapper.readValue(maybeBody.get(), AvaliacaoRequest.class);

            validar(dto);

            context.getLogger().info(
                    String.format("Avaliação recebida com nota %d",
                            dto.nota()));

            avaliacaoService.salvar(dto);

            return request
                    .createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(Map.of(
                            "mensagem", "Avaliação registrada com sucesso"))
                    .build();

        } catch (JsonProcessingException e) {

            context.getLogger().severe(
                    "JSON inválido: " + e.getMessage());

            return request
                    .createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "application/json")
                    .body(Map.of(
                            "erro", "JSON inválido"))
                    .build();

        } catch (IllegalArgumentException e) {

            context.getLogger().warning(
                    "Erro de validação: " + e.getMessage());

            return request
                    .createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "application/json")
                    .body(Map.of(
                            "erro", e.getMessage()))
                    .build();

        } catch (Exception e) {

            context.getLogger().severe(
                    "Erro interno: " + e.getMessage());

            return request
                    .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(Map.of(
                            "erro", "Erro interno ao processar avaliação"))
                    .build();
        }
    }

    private void validar(AvaliacaoRequest dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dados da avaliação são obrigatórios");
        }

        if (dto.descricao() == null || dto.descricao().isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }

        if (dto.nota() == null) {
            throw new IllegalArgumentException("Nota é obrigatória");
        }

        if (dto.nota() < 0 || dto.nota() > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
    }
}