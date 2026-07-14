package com.feedback.report.domain.exception;


/**
 * Exceção personalizada para erros durante a geração de relatórios.
 * Esta classe estende RuntimeException para permitir que seja usada
 * em operações serverless sem necessidade de tratamento obrigatório.
 */
public class ReportGenerationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro
     */
    public ReportGenerationException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem de erro e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param cause Causa original do erro
     */
    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor com causa do erro.
     *
     * @param cause Causa original do erro
     */
    public ReportGenerationException(Throwable cause) {
        super(cause);
    }

    /**
     * Cria uma exceção com código de erro para facilitar monitoramento.
     *
     * @param errorCode Código do erro (ex: "REPORT-001")
     * @param message Mensagem descritiva do erro
     */
    public ReportGenerationException(String errorCode, String message) {
        super(String.format("[%s] %s", errorCode, message));
    }

    /**
     * Cria uma exceção com código de erro, mensagem e causa.
     *
     * @param errorCode Código do erro (ex: "REPORT-002")
     * @param message Mensagem descritiva do erro
     * @param cause Causa original do erro
     */
    public ReportGenerationException(String errorCode, String message, Throwable cause) {
        super(String.format("[%s] %s", errorCode, message), cause);
    }
}
