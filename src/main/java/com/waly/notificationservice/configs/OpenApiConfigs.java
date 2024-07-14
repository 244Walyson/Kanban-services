package com.waly.notificationservice.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class OpenApiConfigs {

  @Bean
  public OpenAPI openApiConfig() {
    return new OpenAPI()
            .info(new Info()
                    .title("GW API")
                    .version("v0.0.1")
                    .license(new License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")));
  }
}
