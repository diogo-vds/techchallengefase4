package org.relatorio.domain.exception;

public class DynamoDBException extends RuntimeException {
    public DynamoDBException(String message, Throwable cause) {
        super(message, cause);
    }
}
