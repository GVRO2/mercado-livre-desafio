package br.com.gabrieloliveira.mercadolivre.usecase;

import br.com.gabrieloliveira.mercadolivre.model.Product;

public interface BuildEtag {
  String execute(Product product);
}
