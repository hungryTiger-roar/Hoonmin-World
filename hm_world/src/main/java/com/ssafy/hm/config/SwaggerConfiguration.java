package com.ssafy.hm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfiguration {

	@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI().info(new Info()
				.title("HM World API")
				.description("""
HM World 모바일 연동용 REST API 문서입니다.
 - Swagger UI: http://localhost:8080/swagger-ui/index.html
 - API 문서(JSON): http://localhost:8080/v3/api-docs
""")
				.version("v1"));
	}
}
