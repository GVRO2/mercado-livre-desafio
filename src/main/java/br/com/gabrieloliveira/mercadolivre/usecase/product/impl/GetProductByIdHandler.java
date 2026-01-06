package br.com.gabrieloliveira.mercadolivre.usecase.product.impl;

import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.usecase.product.GetProductByIdStrategy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GetProductByIdHandler {

  private final Map<String, GetProductByIdStrategy> strategies;

  public GetProductByIdHandler(List<GetProductByIdStrategy> strategies) {
    this.strategies =
        strategies.stream()
            .collect(java.util.stream.Collectors.toMap(GetProductByIdStrategy::name, s -> s));
  }

  public Optional<Product> execute(UUID id, Optional<Boolean> cache) {

    if (cache.isPresent() && cache.get()) {
      Optional<Product> fromRedis = strategies.get("redis").execute(id);
      log.info("Fetching product {} from Redis cache: {}", id, fromRedis.isPresent());
      if (fromRedis.isPresent()) {
        return fromRedis;
      }
    }
    log.info("Fetching product {} from Database", id);
    return strategies.get("db").execute(id);
  }
}
