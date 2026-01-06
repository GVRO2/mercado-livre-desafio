package br.com.gabrieloliveira.mercadolivre.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.repository.ProductRepository;
import br.com.gabrieloliveira.mercadolivre.usecase.product.impl.GetProductByIdStrategyDb;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class GetProductByIdStrategyDbImplTest {

  @Mock private ProductRepository productRepository;
  @Mock private StringRedisTemplate stringRedisTemplate;
  @Mock private ObjectMapper mapper;
  @InjectMocks private GetProductByIdStrategyDb useCase;

  @Test
  void returnsProductWhenRepositoryFindsIt() throws Exception {
    UUID id = UUID.randomUUID();
    Product product = new Product();
    product.setId(id);

    ValueOperations<String, String> ops = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(ops);
    when(mapper.writeValueAsString(product)).thenReturn("{}");

    when(productRepository.findById(id)).thenReturn(Optional.of(product));

    Optional<Product> result = useCase.execute(id);

    assertTrue(result.isPresent());
    assertSame(product, result.get());
    verify(productRepository, times(1)).findById(id);
    verify(stringRedisTemplate).opsForValue();
  }

  @Test
  void returnsEmptyWhenRepositoryDoesNotFindProduct() {
    UUID id = UUID.randomUUID();
    when(productRepository.findById(id)).thenReturn(Optional.empty());

    Optional<Product> result = useCase.execute(id);

    assertFalse(result.isPresent());
    verify(productRepository, times(1)).findById(id);
  }

  @Test
  void throwsWhenRepositoryThrowsException() {
    UUID id = UUID.randomUUID();
    when(productRepository.findById(id)).thenThrow(new RuntimeException("db error"));
    assertThrows(RuntimeException.class, () -> useCase.execute(id));
    verify(productRepository, times(1)).findById(id);
  }
}
