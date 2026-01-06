package br.com.gabrieloliveira.mercadolivre.usecase.product.impl;

import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.usecase.product.GetProductByIdStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetProductByIdStrategyRedis implements GetProductByIdStrategy {

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;


  @Override
  public Optional<Product> execute(UUID id, Optional<String> etag) {
    if (etag.isEmpty()) {
      throw new IllegalArgumentException("ETag must be provided to get from Redis cache");
    }
    try {
      String raw = redis.opsForValue().get(etag.get());
      if (raw == null) {
        return Optional.empty();
      }
      return Optional.of(mapper.readValue(raw, Product.class));
    } catch (Exception e) {
      log.warn("Error getting product from Redis cache {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  @Override
  public String name() {
    return "redis";
  }

  @Override
  public Optional<Product> recover(Class<?> aclass, TransientDataAccessResourceException ex, UUID id) {
    return GetProductByIdStrategy.super.recover(aclass, ex, id);
  }

  @Recover
  public Optional<Product> recover(TransientDataAccessResourceException ex, UUID id) {
    return recover(this.getClass(), ex, id);
  }
}
