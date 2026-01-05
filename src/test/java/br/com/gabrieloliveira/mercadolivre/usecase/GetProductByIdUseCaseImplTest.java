package br.com.gabrieloliveira.mercadolivre.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseImplTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private GetProductByIdUseCaseImpl useCase;

  @Test
  void returnsProductWhenRepositoryFindsIt() {
    UUID id = UUID.randomUUID();
    Product product = new Product();
    product.setId(id);

    when(productRepository.findById(id)).thenReturn(Optional.of(product));

    Optional<Product> result = useCase.execute(id);

    assertTrue(result.isPresent());
    assertSame(product, result.get());
    verify(productRepository, times(1)).findById(id);
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
