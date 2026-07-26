package br.com.postech.techchallenge.fase4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import br.com.postech.techchallenge.fase4.model.Perfil;
import br.com.postech.techchallenge.fase4.model.Usuario;
import br.com.postech.techchallenge.fase4.repository.UsuarioRepository;
import br.com.postech.techchallenge.fase4.service.AuthenticationException;
import br.com.postech.techchallenge.fase4.service.AuthenticationService;

public class AuthenticationServiceTest {

    private final InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
    private final AuthenticationService service = new AuthenticationService(repository);

    @Test
    public void autenticar_deveRetornarEstudanteComCredenciaisValidas() {
        Usuario estudante = usuario("aluno@example.com", "senha123", Perfil.ESTUDANTE);
        repository.salvar(estudante);

        Usuario autenticado = service.autenticar(
                basic("ALUNO@example.com", "senha123"), Perfil.ESTUDANTE);

        assertEquals(estudante.getId(), autenticado.getId());
    }

    @Test
    public void autenticar_deveRejeitarSenhaInvalida() {
        repository.salvar(usuario("aluno@example.com", "senha123", Perfil.ESTUDANTE));

        assertThrows(AuthenticationException.class,
                () -> service.autenticar(
                        basic("aluno@example.com", "errada"), Perfil.ESTUDANTE));
    }

    @Test
    public void autenticar_deveRejeitarPerfilSemPermissao() {
        repository.salvar(usuario("admin@example.com", "senha123", Perfil.ADMINISTRADOR));

        assertThrows(SecurityException.class,
                () -> service.autenticar(
                        basic("admin@example.com", "senha123"), Perfil.ESTUDANTE));
    }

    @Test
    public void autenticar_deveExigirHeaderBasic() {
        assertThrows(AuthenticationException.class,
                () -> service.autenticar(null, Perfil.ESTUDANTE));
    }

    private Usuario usuario(String email, String senha, Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setId("usuario-1");
        usuario.setNome("Usuario");
        usuario.setEmail(email);
        usuario.setSenhaHash(BCrypt.hashpw(senha, BCrypt.gensalt()));
        usuario.setPerfil(perfil);
        return usuario;
    }

    private String basic(String email, String senha) {
        String credenciais = email + ":" + senha;
        return "Basic " + Base64.getEncoder()
                .encodeToString(credenciais.getBytes(StandardCharsets.UTF_8));
    }

    private static class InMemoryUsuarioRepository implements UsuarioRepository {
        private final Map<String, Usuario> usuarios = new HashMap<>();

        @Override
        public Usuario salvar(Usuario usuario) {
            usuarios.put(usuario.getEmail().toLowerCase(), usuario);
            return usuario;
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.ofNullable(usuarios.get(email));
        }
    }
}
