package br.com.gabrieloliveira.mercadolivre.usecase;

import br.com.gabrieloliveira.mercadolivre.model.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public interface GetProductByIdUseCase {
  @Retryable(
      retryFor = {
        TransientDataAccessResourceException.class,
        CannotGetJdbcConnectionException.class
      },
      maxAttempts = 2,
      backoff = @Backoff(delay = 1000, multiplier = 2))
  Optional<Product> execute(UUID id);
}
