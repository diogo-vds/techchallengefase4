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
import br.com.postech.techchallenge.fase4.model.Perfil;
import br.com.postech.techchallenge.fase4.service.AuthenticationException;
import br.com.postech.techchallenge.fase4.service.AuthenticationService;
import br.com.postech.techchallenge.fase4.service.AvaliacaoService;

public class AvaliacaoFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final AuthenticationService authenticationService = new AuthenticationService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context) {

        try {
            var estudante = authenticationService.autenticar(
                    authorizationHeader(request), Perfil.ESTUDANTE);

            String body = request.getBody();
            if (body == null || body.isBlank()) {
                return createErrorResponse(400, "Body da requisicao e obrigatorio");
            }

            AvaliacaoRequest dto = OBJECT_MAPPER.readValue(body, AvaliacaoRequest.class);
            validar(dto);

            var avaliacao = avaliacaoService.salvar(dto, estudante);
            context.getLogger().log("Avaliacao recebida: " + avaliacao.getId());

            return createSuccessResponse(202, Map.of(
                    "mensagem", "Avaliacao recebida para processamento",
                    "id", avaliacao.getId()));
        } catch (JsonProcessingException e) {
            return createErrorResponse(400, "JSON invalido");
        } catch (AuthenticationException e) {
            return createErrorResponse(401, e.getMessage(), true);
        } catch (SecurityException e) {
            return createErrorResponse(403, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(400, e.getMessage());
        } catch (Exception e) {
            context.getLogger().log("Erro interno: " + e.getMessage());
            return createErrorResponse(500, "Erro interno ao processar avaliacao");
        }
    }

    private String authorizationHeader(APIGatewayProxyRequestEvent request) {
        if (request.getHeaders() == null) {
            return null;
        }
        return request.getHeaders().entrySet().stream()
                .filter(entry -> "authorization".equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private void validar(AvaliacaoRequest dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados da avaliacao sao obrigatorios");
        }
        if (dto.descricao() == null || dto.descricao().isBlank()) {
            throw new IllegalArgumentException("Descricao e obrigatoria");
        }
        if (dto.nota() == null) {
            throw new IllegalArgumentException("Nota e obrigatoria");
        }
        if (dto.nota() < 0 || dto.nota() > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String errorMessage) {
        return createErrorResponse(statusCode, errorMessage, false);
    }

    private APIGatewayProxyResponseEvent createErrorResponse(
            int statusCode,
            String errorMessage,
            boolean basicChallenge) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        if (basicChallenge) {
            headers.put("WWW-Authenticate", "Basic realm=\"api-avaliacao\"");
        }
        return response(statusCode, Map.of("erro", errorMessage), headers);
    }

    private APIGatewayProxyResponseEvent createSuccessResponse(int statusCode, Map<String, ?> data) {
        return response(statusCode, data, Map.of("Content-Type", "application/json"));
    }

    private APIGatewayProxyResponseEvent response(
            int statusCode,
            Map<String, ?> body,
            Map<String, String> headers) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setHeaders(headers);
        try {
            response.setBody(OBJECT_MAPPER.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            response.setStatusCode(500);
            response.setBody("{\"erro\":\"Erro ao serializar resposta\"}");
        }
        return response;
    }
}
