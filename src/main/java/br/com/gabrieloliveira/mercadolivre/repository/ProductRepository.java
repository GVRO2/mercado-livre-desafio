package br.com.gabrieloliveira.mercadolivre.repository;

import br.com.gabrieloliveira.mercadolivre.model.Product;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
  @Transactional
  void deleteById(Long id);
}
