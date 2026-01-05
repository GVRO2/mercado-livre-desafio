package br.com.gabrieloliveira.mercadolivre.usecase;

import br.com.gabrieloliveira.mercadolivre.exception.DatabaseConnectException;
import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetProductByIdUseCaseImpl implements GetProductByIdUseCase {

  private final ProductRepository productRepository;

  @Override
  public Optional<Product> execute(UUID id) {
    return productRepository.findById(id);
  }

  @Recover
  public Optional<Product> recover(TransientDataAccessResourceException ex, UUID id) {
    String method = String.format("GetProductByIdUseCaseImpl.execute(%s)", id);
    throw new DatabaseConnectException(ex, method);
  }
}
