package br.com.gabrieloliveira.mercadolivre.exception;

import br.com.gabrieloliveira.mercadolivre.model.ApiError;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  private final MessageSource messageSource;

  private static final String MESSAGE_PATTERN = "{} | {}: {}";

  public ApiExceptionHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, Locale locale) {
    String message =
        messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), ex.getMessageKey(), locale);
    log.error(MESSAGE_PATTERN, ex.getHttpStatus(), ex.getMessageKey(), message, ex);
    return ResponseEntity.status(ex.getHttpStatus())
        .body(new ApiError(ex.getMessageKey(), message));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, Locale locale) {
      if (Objects.requireNonNull(ex.getValue()) instanceof String && ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) {
          return handleInvalidUuidFormatException(new InvalidUuidFormatException(ex,""), locale);
      }
      String message = String.format("Invalid value '%s' for parameter '%s'.", ex.getValue(), ex.getName());
      log.warn(MESSAGE_PATTERN, HttpStatus.BAD_REQUEST, ex.getClass().getSimpleName(), message, ex);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new ApiError("VALIDATION_ERROR", message));
  }
  private ResponseEntity<ApiError> handleInvalidUuidFormatException(InvalidUuidFormatException ex, Locale locale) {
    String message =
        messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), ex.getMessageKey(), locale);
    log.warn(MESSAGE_PATTERN, HttpStatus.BAD_REQUEST, ex.getMessageKey(), message, ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError(ex.getMessageKey(), message));
  }
}
