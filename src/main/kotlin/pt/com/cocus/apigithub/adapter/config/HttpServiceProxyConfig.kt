package pt.com.cocus.apigithub.adapter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class HttpServiceProxyConfig {

    @Bean
    fun webClient(builder: WebClient.Builder) : WebClient = builder.baseUrl("\${api-github.url}").build()

}