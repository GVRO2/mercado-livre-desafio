package br.com.gabrieloliveira.mercadolivre.config;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
@Profile("test")
public class TestConfiguration {

  @Bean
  public TestRestTemplate testRestTemplate() {

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    TestRestTemplate restTemplate = new TestRestTemplate();
    restTemplate.getRestTemplate().setRequestFactory(requestFactory);

    return restTemplate;
  }
}
