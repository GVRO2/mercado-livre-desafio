package br.com.gabrieloliveira.mercadolivre.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public class InvalidUuidFormatException extends BusinessException {
    public InvalidUuidFormatException(MethodArgumentTypeMismatchException ex,String parameterName) {
        super(ex, "invalid.uuid.format", HttpStatus.BAD_REQUEST,parameterName);
    }
}
