package br.com.postech.techchallenge.fase4.function;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.postech.techchallenge.fase4.model.AvaliacaoRequest;
import br.com.postech.techchallenge.fase4.service.AvaliacaoService;

public class AvaliacaoFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final AvaliacaoService avaliacaoService = new AvaliacaoService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context) {

        return run(request, context);
    }

    private APIGatewayProxyResponseEvent run(
            APIGatewayProxyRequestEvent request,
            Context context) {

        try {
            context.getLogger().log("Recebendo nova avaliação");

            String body = request.getBody();

            if (body == null || body.isBlank()) {
                return createErrorResponse(400, "Body da requisição é obrigatório");
            }

            AvaliacaoRequest dto = objectMapper.readValue(body, AvaliacaoRequest.class);

            validar(dto);

            context.getLogger().log(
                    String.format("Avaliação recebida com nota %d", dto.nota()));

            avaliacaoService.salvar(dto);

            return createSuccessResponse(201, 
                    Map.of("mensagem", "Avaliação registrada com sucesso"));

        } catch (JsonProcessingException e) {
            context.getLogger().log("JSON inválido: " + e.getMessage());
            return createErrorResponse(400, "JSON inválido");

        } catch (IllegalArgumentException e) {
            context.getLogger().log("Erro de validação: " + e.getMessage());
            return createErrorResponse(400, e.getMessage());

        } catch (Exception e) {
            context.getLogger().log("Erro interno: " + e.getMessage());
            return createErrorResponse(500, "Erro interno ao processar avaliação");
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

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String errorMessage) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("erro", errorMessage);
            
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(statusCode);
            response.setBody(objectMapper.writeValueAsString(body));
            response.setHeaders(Map.of("Content-Type", "application/json"));
            return response;
        } catch (JsonProcessingException e) {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(500);
            response.setBody("{\"erro\":\"Erro ao serializar resposta\"}");
            response.setHeaders(Map.of("Content-Type", "application/json"));
            return response;
        }
    }

    private APIGatewayProxyResponseEvent createSuccessResponse(int statusCode, Map<String, ?> data) {
        try {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(statusCode);
            response.setBody(objectMapper.writeValueAsString(data));
            response.setHeaders(Map.of("Content-Type", "application/json"));
            return response;
        } catch (JsonProcessingException e) {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(500);
            response.setBody("{\"erro\":\"Erro ao serializar resposta\"}");
            response.setHeaders(Map.of("Content-Type", "application/json"));
            return response;
        }
    }
}