package br.com.gabrieloliveira.mercadolivre.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.context.MessageSource;

@EnableRetry
@Configuration
public class ApplicationConfig {

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:message");
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setUseCodeAsDefaultMessage(false);
    return messageSource;
  }
}
