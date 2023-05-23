package pt.com.cocus.apigithub.adapter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class HttpServiceProxyConfig {

    @Value("\${api-github.url}")
    lateinit var urlBase: String
    @Bean
    fun webClient(builder: WebClient.Builder) : WebClient = builder.baseUrl(urlBase).build()

}