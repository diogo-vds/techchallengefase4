package br.com.postech.techchallenge.fase4.repository;

import java.util.Optional;

import br.com.postech.techchallenge.fase4.model.Usuario;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);
}
