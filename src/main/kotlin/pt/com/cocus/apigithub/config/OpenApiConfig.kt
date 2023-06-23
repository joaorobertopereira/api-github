package pt.com.cocus.apigithub.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OpenApiConfig {
    @Bean
    open fun customOpenApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("API with Kotlin 1.8.21 and Spring Boot 2.7.13")
                    .version("v1")
                    .description("Cocus Backend Kotlin Task")

                    .termsOfService("https://cocus.com.pt")
                    .license(
                        License().name("Apache 2.0")
                            .url("https://cocus.com.pt")
                    )
            )
    }

}