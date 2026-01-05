package br.com.gabrieloliveira.mercadolivre.controller;

import br.com.gabrieloliveira.mercadolivre.model.Model;
import br.com.gabrieloliveira.mercadolivre.service.ModelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class ModelController {

  private ModelService modelService;

  @GetMapping("/")
  public String home() {
    return "Default Java 21 Project Home Page";
  }

  @PostMapping(value = "/model", consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewModel(@RequestBody @Valid Model model) {
    /* write your code here */
  }

  @DeleteMapping(value = "/erase")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAllModels() {
    /* write your code here */
  }

  @DeleteMapping(value = "/model/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteModelById(@RequestParam Long id) {
    /* write your code here */
  }

  @GetMapping(value = "/model")
  @ResponseStatus(HttpStatus.OK)
  public List<Model> getAllModels() {
    /* write your code here */
    return List.of();
  }

  @GetMapping(value = "/model/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Model getModelById(Long id) {
    /* write your code here */
    return null;
  }
}
