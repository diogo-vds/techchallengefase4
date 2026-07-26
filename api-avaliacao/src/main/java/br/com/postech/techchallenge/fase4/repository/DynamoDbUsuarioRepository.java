package br.com.postech.techchallenge.fase4.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import br.com.postech.techchallenge.fase4.integration.AwsClientFactory;
import br.com.postech.techchallenge.fase4.model.Perfil;
import br.com.postech.techchallenge.fase4.model.Usuario;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class DynamoDbUsuarioRepository implements UsuarioRepository {

    private final DynamoDbClient client;
    private final String tableName;

    public DynamoDbUsuarioRepository() {
        this(AwsClientFactory.dynamoDbClient(), env("USUARIOS_TABLE", "usuarios"));
    }

    DynamoDbUsuarioRepository(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", string(usuario.getEmail()));
        item.put("id", string(usuario.getId()));
        item.put("nome", string(usuario.getNome()));
        item.put("senhaHash", string(usuario.getSenhaHash()));
        item.put("perfil", string(usuario.getPerfil().name()));

        client.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .conditionExpression("attribute_not_exists(email)")
                .build());
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        Map<String, AttributeValue> key = Map.of("email", string(email));
        Map<String, AttributeValue> item = client.getItem(GetItemRequest.builder()
                        .tableName(tableName)
                        .key(key)
                        .consistentRead(true)
                        .build())
                .item();

        if (item == null || item.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(item.get("email").s());
        usuario.setId(item.get("id").s());
        usuario.setNome(item.get("nome").s());
        usuario.setSenhaHash(item.get("senhaHash").s());
        usuario.setPerfil(Perfil.valueOf(item.get("perfil").s()));
        return Optional.of(usuario);
    }

    private static AttributeValue string(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
