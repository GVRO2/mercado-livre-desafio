package br.com.gabrieloliveira.mercadolivre.usecase.product;

import br.com.gabrieloliveira.mercadolivre.exception.DatabaseConnectException;
import br.com.gabrieloliveira.mercadolivre.model.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public interface GetProductByIdStrategy {
  @Retryable(
      retryFor = {
        TransientDataAccessResourceException.class,
        CannotGetJdbcConnectionException.class,
        RedisConnectionFailureException.class,
        QueryTimeoutException.class,
        RedisSystemException.class
      },
      maxAttempts = 2,
      backoff = @Backoff(delay = 1000, multiplier = 2))
  Optional<Product> execute(UUID id, Optional<String> etag);
  String name();

   default Optional<Product> recover(Class<?> aclass,TransientDataAccessResourceException ex, UUID id) {
       String method = String.format("%s.execute(%s)", aclass.getSimpleName(), id);
       throw new DatabaseConnectException(ex, method);
    }
}
