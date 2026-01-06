package br.com.gabrieloliveira.mercadolivre.usecase;

import br.com.gabrieloliveira.mercadolivre.model.ApiError;
import br.com.gabrieloliveira.mercadolivre.model.Price;
import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.model.Rating;
import br.com.gabrieloliveira.mercadolivre.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GetProductByIdStrategyIT extends RedisTestContainer {

  @MockitoBean private ProductRepository productRepository;

  @MockitoBean private StringRedisTemplate stringRedisTemplate;

  @Autowired private TestRestTemplate rest;

  @Autowired private ObjectMapper objectMapper;

  @LocalServerPort private int port;

  private static String buildUrl(int port) {
    return String.format("http://localhost:%d/api/v1/products/{id}", port);
  }

  @Test
  void shouldReturnProductByIdInDbWithoutCache() {
    UUID id = UUID.randomUUID();
    ValueOperations<String,String> mock = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(mock);
    when(productRepository.findById(id)).thenReturn(Optional.of(createProduct(id)));

    ResponseEntity<Product> response = rest.getForEntity(buildUrl(port), Product.class, id);

    verify(productRepository).findById(any(UUID.class));
    verify(mock).set(anyString(), anyString(), any());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void shouldReturnProductByIdInCacheTrue() {
    UUID id = UUID.randomUUID();

    ValueOperations<String,String> mock = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(mock);
    when(mock.get(id.toString()))
        .thenReturn(
            "{\"id\":\""
                + id
                + "\",\"name\":\"Product Name\",\"imageUrl\":\"http://urlmock.com.br\",\"description\":\"Product Description\",\"price\":{\"amount\":99.9,\"currency\":\"BRL\"},\"rating\":{\"average\":4.5,\"count\":10},\"specification\":{\"storage\":\"256GB\",\"ram\":\"8GB\"},\"createdAt\":\"2024-06-01T00:00:00Z\"}");

    HttpHeaders headers = new HttpHeaders();
    headers.set("cache", "true");
    HttpEntity<Void> entity = new HttpEntity<>(headers);
    ResponseEntity<Product> response =
        rest.exchange(buildUrl(port), HttpMethod.GET, entity, Product.class, id);

    verify(productRepository, never()).findById(any(UUID.class));
    verify(mock).get(id.toString());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void shouldReturnProductByIdInCacheFalse() {
    ValueOperations<String, String> mock = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(mock);
    doNothing().when(mock).set(anyString(), anyString(), any());

    UUID id = UUID.randomUUID();
    when(productRepository.findById(id)).thenReturn(Optional.of(createProduct(id)));

    HttpHeaders headers = new HttpHeaders();
    headers.set("cache", "false");
    HttpEntity<Void> entity = new HttpEntity<>(headers);
    ResponseEntity<Product> response =
        rest.exchange(buildUrl(port), HttpMethod.GET, entity, Product.class, id);

    verify(productRepository).findById(any(UUID.class));
    verify(mock).set(anyString(), anyString(), any());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void shouldReturn404WhenProductDoesNotExist() {
    UUID id = UUID.randomUUID();
    ResponseEntity<Product> response = rest.getForEntity(buildUrl(port), Product.class, id);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void shouldReturnProductByIdInCacheTrueButNothingReturn() {
    UUID id = UUID.randomUUID();
    ValueOperations<String,String> mock = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(mock);
    when(mock.get(id.toString()))
            .thenReturn(null);
    when(productRepository.findById(id)).thenReturn(Optional.of(createProduct(id)));

    HttpHeaders headers = new HttpHeaders();
    headers.set("cache", "true");
    HttpEntity<Void> entity = new HttpEntity<>(headers);
    ResponseEntity<Product> response =
            rest.exchange(buildUrl(port), HttpMethod.GET, entity, Product.class, id);

    verify(mock).get(id.toString());
    verify(productRepository).findById(any(UUID.class));
    verify(mock).set(anyString(), anyString(), any());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void shouldReturn400WhenUUIDIsInvalid() {
    String invalidId = "invalid-uuid";
    ResponseEntity<ApiError> response =
        rest.getForEntity(buildUrl(port), ApiError.class, invalidId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("invalid.uuid.format", response.getBody().getCode());
  }
  private Product createProduct(UUID id) {
    Map<String, String> specification = new HashMap<>();
    specification.put("storage", "256GB");
    specification.put("ram", "8GB");
    Rating rating = new Rating(4.5, 10);
    return new Product(
        id,
        "Product Name",
        URI.create("http://urlmock.com.br"),
        "Product Description",
        new Price(BigDecimal.valueOf(99.90), Currency.getInstance("BRL")),
        rating,
        specification,
        OffsetDateTime.now());
  }
}
