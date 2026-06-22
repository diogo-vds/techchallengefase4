package br.com.postech.techchallenge.fase4.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.postech.techchallenge.fase4.model.Avaliacao;

public class JsonAvaliacaoRepository implements AvaliacaoRepository {

    private static final String FILE_NAME = "data/feedbacks.json";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Avaliacao salvar(Avaliacao avaliacao) {

        try {

            File file = new File(FILE_NAME);

            // garante que o diretório pai exista (ex: data/)
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            List<Avaliacao> avaliacoes = new ArrayList<>();

            if (file.exists()) {
                avaliacoes = mapper.readValue(
                        file,
                        new TypeReference<List<Avaliacao>>() {
                        });
            }

            avaliacoes.add(avaliacao);

            // escreve em arquivo temporário e move para evitar escritas parciais
            Path targetPath = file.toPath();
            Path parentPath = targetPath.getParent();
            if (parentPath == null) {
                parentPath = targetPath.getRoot();
            }

            Path tmp = Files.createTempFile(parentPath, "feedbacks", ".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(tmp.toFile(), avaliacoes);
            Files.move(tmp, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

            return avaliacao;

        } catch (IOException e) {

            throw new RuntimeException("Erro ao salvar avaliação", e);
        }
    }
}