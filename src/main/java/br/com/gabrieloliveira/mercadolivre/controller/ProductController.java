package br.com.gabrieloliveira.mercadolivre.controller;

import br.com.gabrieloliveira.mercadolivre.api.ProductApi;
import br.com.gabrieloliveira.mercadolivre.model.*;
import java.util.Optional;
import java.util.UUID;

import br.com.gabrieloliveira.mercadolivre.usecase.product.impl.GetProductByIdHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ProductController implements ProductApi {

  private final GetProductByIdHandler getProductByIdHandler;

  @Override
  public ResponseEntity<Product> getProductById(UUID productId, Optional<String> ifNoneMatch) {
    Optional<Product> product = getProductByIdHandler.execute(productId, ifNoneMatch);
    if (product.isPresent()) {
      return ResponseEntity.ok(product.get());
    }
    return ResponseEntity.notFound().build();
  }

  //  @Override
  //  public ResponseEntity<Product> createProduct(CreateProductRequest createProductRequest) {
  //    return ProductApi.super.createProduct(createProductRequest);
  //  }
  //
  //  @Override
  //  public ResponseEntity<Void> deleteProduct(String productId) {
  //    return ProductApi.super.deleteProduct(productId);
  //  }
  //
  //  @Override
  //  public ResponseEntity<GetProducts200Response> getProducts(
  //      String ids, Integer page, Integer size, String sort, String locale, String currency) {
  //    return ProductApi.super.getProducts(ids, page, size, sort, locale, currency);
  //  }
  //
  //  @Override
  //  public ResponseEntity<Product> patchProduct(
  //      String productId, PatchProductRequest patchProductRequest, String ifMatch) {
  //    return ProductApi.super.patchProduct(productId, patchProductRequest, ifMatch);
  //  }
  //
  //  @Override
  //  public ResponseEntity<Product> replaceProduct(
  //      String productId, UpdateProductRequest updateProductRequest, String ifMatch) {
  //    return ProductApi.super.replaceProduct(productId, updateProductRequest, ifMatch);
  //  }
}
