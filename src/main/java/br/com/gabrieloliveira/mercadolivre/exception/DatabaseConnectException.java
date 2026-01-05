package br.com.gabrieloliveira.mercadolivre.exception;

import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpStatus;

public class DatabaseConnectException extends BusinessException {
  public DatabaseConnectException(TransientDataAccessResourceException ex, String method) {
    super(ex, "database.connection.error", HttpStatus.SERVICE_UNAVAILABLE, method);
  }
}
