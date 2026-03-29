package com.memorygrove.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Swagger
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MemoryGrove API",
                version = "1.0",
                description = "API для веб-сервиса MemoryGrove"
        )
)
public class OpenApiConfiguration {
}
