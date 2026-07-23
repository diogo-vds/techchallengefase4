package org.relatorio.domain.exception;

public class RelatorioNaoEncontradoException extends RuntimeException {
    public RelatorioNaoEncontradoException(Long id) {
        super(String.format("Relatório com ID %d não encontrado", id));
    }
}
