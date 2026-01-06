package br.com.gabrieloliveira.mercadolivre.exception;

import br.com.gabrieloliveira.mercadolivre.model.ApiError;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  private final MessageSource messageSource;

  public ApiExceptionHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, Locale locale) {
    String message =
        messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), ex.getMessageKey(), locale);
    log.error("{} | {}: {}", ex.getHttpStatus(), ex.getMessageKey(), message, ex);
    return ResponseEntity.status(ex.getHttpStatus())
        .body(new ApiError(ex.getMessageKey(), message));
  }
}
