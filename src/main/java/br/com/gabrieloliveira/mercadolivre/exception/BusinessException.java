package br.com.gabrieloliveira.mercadolivre.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

  private final String messageKey;
  private final String[] args;
  private final HttpStatus httpStatus;

  public BusinessException(
      Throwable cause, String messageKey, HttpStatus httpStatus, String... args) {
    super(cause);
    this.messageKey = messageKey;
    this.httpStatus = httpStatus;
    this.args = args;
  }
}
