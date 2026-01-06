package br.com.gabrieloliveira.mercadolivre.usecase.product.impl;

import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.repository.ProductRepository;
import br.com.gabrieloliveira.mercadolivre.usecase.BuildEtag;
import br.com.gabrieloliveira.mercadolivre.usecase.product.GetProductByIdStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetProductByIdStrategyDb implements GetProductByIdStrategy {

     private final ProductRepository productRepository;
     private final StringRedisTemplate redis;
     private final ObjectMapper mapper;
     private final BuildEtag buildEtag;

  @Override
  public Optional<Product> execute(UUID id, Optional<String> etag) {
    Optional<Product> product = productRepository.findById(id);
    product.ifPresent(value -> putInRedis(buildEtag.execute(value), value));
    return product;
  }

  @Override
  public String name() {
    return "db";
  }

  private void putInRedis(String key, Product product) {
    try {
      redis.opsForValue().set(key, mapper.writeValueAsString(product), Duration.ofHours(1));
    } catch (Exception e) {
      log.warn("Error setting product from Redis cache {}", e.getMessage(), e);
    }
  }

  @Recover
  public Optional<Product> recover(TransientDataAccessResourceException ex, UUID id) {
    return recover(this.getClass(), ex, id);
  }
}
