package br.com.postech.techchallenge.fase4.function;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.postech.techchallenge.fase4.model.UsuarioRequest;
import br.com.postech.techchallenge.fase4.service.UsuarioService;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

public class UsuarioFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context) {

        try {
            if (request.getBody() == null || request.getBody().isBlank()) {
                return response(400, Map.of("erro", "Body da requisicao e obrigatorio"));
            }
            UsuarioRequest dto = OBJECT_MAPPER.readValue(request.getBody(), UsuarioRequest.class);
            var usuario = usuarioService.cadastrar(dto);
            return response(201, Map.of(
                    "id", usuario.getId(),
                    "nome", usuario.getNome(),
                    "email", usuario.getEmail(),
                    "perfil", usuario.getPerfil().name()));
        } catch (JsonProcessingException e) {
            return response(400, Map.of("erro", "JSON invalido"));
        } catch (IllegalArgumentException e) {
            return response(400, Map.of("erro", e.getMessage()));
        } catch (ConditionalCheckFailedException e) {
            return response(409, Map.of("erro", "Email ja cadastrado"));
        } catch (Exception e) {
            context.getLogger().log("Erro ao cadastrar usuario: " + e.getMessage());
            return response(500, Map.of("erro", "Erro interno ao cadastrar usuario"));
        }
    }

    private APIGatewayProxyResponseEvent response(int statusCode, Map<String, ?> body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setHeaders(Map.of("Content-Type", "application/json"));
        try {
            response.setBody(OBJECT_MAPPER.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            response.setStatusCode(500);
            response.setBody("{\"erro\":\"Erro ao serializar resposta\"}");
        }
        return response;
    }
}
