package br.com.postech.techchallenge.fase4.service;

import java.util.Locale;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import br.com.postech.techchallenge.fase4.model.Perfil;
import br.com.postech.techchallenge.fase4.model.Usuario;
import br.com.postech.techchallenge.fase4.model.UsuarioRequest;
import br.com.postech.techchallenge.fase4.repository.DynamoDbUsuarioRepository;
import br.com.postech.techchallenge.fase4.repository.UsuarioRepository;

public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService() {
        this(new DynamoDbUsuarioRepository());
    }

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario cadastrar(UsuarioRequest request) {
        validar(request);

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID().toString());
        usuario.setNome(request.nome().trim());
        usuario.setEmail(normalizarEmail(request.email()));
        usuario.setSenhaHash(BCrypt.hashpw(request.senha(), BCrypt.gensalt()));
        usuario.setPerfil(request.perfil());
        return repository.salvar(usuario);
    }

    private void validar(UsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do usuario sao obrigatorios");
        }
        if (request.nome() == null || request.nome().isBlank()) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }
        if (request.email() == null || request.email().isBlank()
                || !request.email().contains("@")) {
            throw new IllegalArgumentException("Email invalido");
        }
        if (request.senha() == null || request.senha().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter ao menos 6 caracteres");
        }
        if (request.perfil() == null) {
            throw new IllegalArgumentException("Perfil e obrigatorio");
        }
        if (request.perfil() == Perfil.ADMINISTRADOR) {
            throw new IllegalArgumentException(
                    "Administradores nao podem ser cadastrados pelo endpoint publico");
        }
    }

    static String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
