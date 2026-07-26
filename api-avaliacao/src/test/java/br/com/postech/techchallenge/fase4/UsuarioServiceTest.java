package br.com.postech.techchallenge.fase4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import br.com.postech.techchallenge.fase4.model.Perfil;
import br.com.postech.techchallenge.fase4.model.Usuario;
import br.com.postech.techchallenge.fase4.model.UsuarioRequest;
import br.com.postech.techchallenge.fase4.repository.UsuarioRepository;
import br.com.postech.techchallenge.fase4.service.UsuarioService;

public class UsuarioServiceTest {

    @Test
    public void cadastrar_deveNormalizarEmailEAplicarHashNaSenha() {
        RecordingRepository repository = new RecordingRepository();
        UsuarioService service = new UsuarioService(repository);

        Usuario usuario = service.cadastrar(new UsuarioRequest(
                "Maria", " Maria@Example.COM ", "senha123", Perfil.ESTUDANTE));

        assertEquals("maria@example.com", usuario.getEmail());
        assertNotEquals("senha123", usuario.getSenhaHash());
        assertTrue(BCrypt.checkpw("senha123", usuario.getSenhaHash()));
    }

    @Test
    public void cadastrar_deveImpedirAdministradorNoEndpointPublico() {
        UsuarioService service = new UsuarioService(new RecordingRepository());

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(
                new UsuarioRequest("Admin", "admin@example.com", "senha123",
                        Perfil.ADMINISTRADOR)));
    }

    private static class RecordingRepository implements UsuarioRepository {
        @Override
        public Usuario salvar(Usuario usuario) {
            return usuario;
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.empty();
        }
    }
}
