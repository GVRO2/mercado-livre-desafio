package br.com.gabrieloliveira.mercadolivre.service;

import br.com.gabrieloliveira.mercadolivre.model.Model;

import java.util.List;

public interface ModelService {
  void deleteAllModels();

  void deleteModelById(Long id);

  void createModel(Model model);

  Model getModelById(Long id);

  List<Model> getAllModels();
}
