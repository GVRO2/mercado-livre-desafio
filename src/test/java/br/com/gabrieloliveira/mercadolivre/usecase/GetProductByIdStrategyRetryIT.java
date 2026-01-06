package br.com.gabrieloliveira.mercadolivre.usecase;

import br.com.gabrieloliveira.mercadolivre.model.ApiError;
import br.com.gabrieloliveira.mercadolivre.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GetProductByIdStrategyRetryIT extends RedisTestContainer {

  @MockitoBean private ProductRepository productRepository;

  @MockitoBean private StringRedisTemplate stringRedisTemplate;

  @Autowired private TestRestTemplate rest;

  @Autowired private ObjectMapper objectMapper;

  @LocalServerPort private int port;

  private static String buildUrl(int port) {
    return String.format("http://localhost:%d/api/v1/products/{id}", port);
  }

  @ParameterizedTest
  @ValueSource(strings = {"es", "pt-BR", "en"})
  void shouldReturn503WhenDatabaseIsDown(String locale) {
    UUID id = UUID.randomUUID();
    when(productRepository.findById(id))
        .thenThrow(new TransientDataAccessResourceException("Database is down"));

    String url = buildUrl(port) + "?locale=" + locale;
    ResponseEntity<ApiError> response = rest.getForEntity(url, ApiError.class, id);

    verify(productRepository, times(2)).findById(any(UUID.class));
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("database.connection.error", response.getBody().getCode());
  }

}
