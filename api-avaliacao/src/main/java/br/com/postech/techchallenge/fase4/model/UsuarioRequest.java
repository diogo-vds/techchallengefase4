package br.com.postech.techchallenge.fase4.model;

public record UsuarioRequest(
        String nome,
        String email,
        String senha,
        Perfil perfil
) {
}
