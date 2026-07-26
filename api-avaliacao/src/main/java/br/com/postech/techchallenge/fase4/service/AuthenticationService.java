package br.com.postech.techchallenge.fase4.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;

import br.com.postech.techchallenge.fase4.model.Perfil;
import br.com.postech.techchallenge.fase4.model.Usuario;
import br.com.postech.techchallenge.fase4.repository.DynamoDbUsuarioRepository;
import br.com.postech.techchallenge.fase4.repository.UsuarioRepository;

public class AuthenticationService {

    private final UsuarioRepository repository;

    public AuthenticationService() {
        this(new DynamoDbUsuarioRepository());
    }

    public AuthenticationService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario autenticar(String authorizationHeader, Perfil perfilNecessario) {
        Credenciais credenciais = extrairCredenciais(authorizationHeader);
        Usuario usuario = repository.buscarPorEmail(
                        UsuarioService.normalizarEmail(credenciais.email()))
                .orElseThrow(() -> new AuthenticationException("Credenciais invalidas"));

        if (!BCrypt.checkpw(credenciais.senha(), usuario.getSenhaHash())) {
            throw new AuthenticationException("Credenciais invalidas");
        }
        if (usuario.getPerfil() != perfilNecessario) {
            throw new SecurityException("Usuario nao possui permissao para esta operacao");
        }
        return usuario;
    }

    private Credenciais extrairCredenciais(String header) {
        if (header == null || !header.regionMatches(true, 0, "Basic ", 0, 6)) {
            throw new AuthenticationException("Autenticacao Basic obrigatoria");
        }

        try {
            String valor = new String(
                    Base64.getDecoder().decode(header.substring(6).trim()),
                    StandardCharsets.UTF_8);
            int separador = valor.indexOf(':');
            if (separador <= 0) {
                throw new IllegalArgumentException();
            }
            return new Credenciais(valor.substring(0, separador), valor.substring(separador + 1));
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Cabecalho de autenticacao invalido");
        }
    }

    private record Credenciais(String email, String senha) {
    }
}
