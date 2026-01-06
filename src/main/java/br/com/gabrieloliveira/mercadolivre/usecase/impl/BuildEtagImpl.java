package br.com.gabrieloliveira.mercadolivre.usecase.impl;

import br.com.gabrieloliveira.mercadolivre.model.Product;
import br.com.gabrieloliveira.mercadolivre.usecase.BuildEtag;
import org.springframework.stereotype.Component;

@Component
public class BuildEtagImpl implements BuildEtag {
  @Override
  public String execute(Product product) {
    return String.format("product-%s-%s", product.getId(), product.getLastUpdated());
  }
}
