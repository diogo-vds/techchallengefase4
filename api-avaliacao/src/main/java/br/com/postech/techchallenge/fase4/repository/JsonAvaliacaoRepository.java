package br.com.postech.techchallenge.fase4.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.postech.techchallenge.fase4.model.Avaliacao;

public class JsonAvaliacaoRepository implements AvaliacaoRepository {

    private static final String FILE_NAME = "data/feedbacks.json";

        private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public Avaliacao salvar(Avaliacao avaliacao) {


        System.out.println("####Entrou no repository");

        try {


            // Resolve path to project-level `data/` by searching for pom.xml upwards
            Path targetPath = resolveProjectPath(FILE_NAME);
            System.out.println("Arquivo absoluto: " + targetPath.toAbsolutePath());

            Path parentPath = targetPath.getParent();
            if (parentPath != null && !Files.exists(parentPath)) {
                Files.createDirectories(parentPath);
            }

            List<Avaliacao> avaliacoes = new ArrayList<>();

            if (Files.exists(targetPath) && Files.size(targetPath) > 0) {
                avaliacoes = mapper.readValue(
                        targetPath.toFile(),
                        new TypeReference<List<Avaliacao>>() {
                        });
            }

            avaliacoes.add(avaliacao);

            // escreve em arquivo temporário e move para evitar escritas parciais
            if (parentPath == null) {
                parentPath = targetPath.getRoot();
            }

            Path tmp = Files.createTempFile(parentPath, "feedbacks", ".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(tmp.toFile(), avaliacoes);
            Files.move(tmp, targetPath, StandardCopyOption.REPLACE_EXISTING);

            return avaliacao;

        } catch (IOException e) {

            throw new RuntimeException("Erro ao salvar avaliação", e);
        }
    }

    private Path resolveProjectPath(String relative) {
        Path cur = Paths.get("").toAbsolutePath();
        Path p = cur;
        while (p != null) {
            if (Files.exists(p.resolve("pom.xml"))) {
                return p.resolve(relative);
            }
            p = p.getParent();
        }
        // fallback to current working directory
        return cur.resolve(relative);
    }
}