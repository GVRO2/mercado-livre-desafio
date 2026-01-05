package br.com.gabrieloliveira.mercadolivre.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.gabrieloliveira.mercadolivre.model.ApiError;
import br.com.gabrieloliveira.mercadolivre.model.Price;
import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.model.Rating;
import br.com.gabrieloliveira.mercadolivre.repository.ProductRepository;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GetProductByIdUseCaseRetryIT {

  @MockitoBean private ProductRepository productRepository;

  @Autowired private TestRestTemplate rest;

  @LocalServerPort private int port;

  private static String buildUrl(int port) {
    return String.format("http://localhost:%d/api/v1/products/{id}", port);
  }

  @Test
  void shouldReturnProductById() {
    UUID id = UUID.randomUUID();
    when(productRepository.findById(id)).thenReturn(Optional.of(createProduct(id)));
    ResponseEntity<Product> response = rest.getForEntity(buildUrl(port), Product.class, id);
    verify(productRepository).findById(any(UUID.class));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void shouldReturn404WhenProductDoesNotExist() {
    UUID id = UUID.randomUUID();
    ResponseEntity<Product> response = rest.getForEntity(buildUrl(port), Product.class, id);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @ParameterizedTest
  @ValueSource(strings = {"es", "pt-BR", "en"})
  void shouldReturn503WhenDatabaseIsDown(String locale) {
    UUID id = UUID.randomUUID();
    when(productRepository.findById(id)).thenThrow(new TransientDataAccessResourceException("Database is down"));
    String url = buildUrl(port) + "?locale=" + locale;
    ResponseEntity<ApiError> response = rest.getForEntity(url, ApiError.class, id);
    verify(productRepository,times(2)).findById(any(UUID.class));
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("database.connection.error", response.getBody().getCode());
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
        specification);
  }
}
