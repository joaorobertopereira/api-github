package pt.com.cocus.apigithub.interceptor

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import pt.com.cocus.apigithub.exceptions.ExceptionResponse
import reactor.core.publisher.Mono

@Component
class AcceptHeaderInterceptor : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val acceptHeader = exchange.request.headers[HttpHeaders.ACCEPT]

        if (acceptHeader != null && acceptHeader.any { it.equals(MediaType.APPLICATION_XML_VALUE, ignoreCase = true) }) {
            val response = exchange.response
            response.statusCode = HttpStatus.NOT_ACCEPTABLE
            response.headers.contentType = MediaType.APPLICATION_JSON

            val errorMessage = ExceptionResponse(409,"Error: application/xml is not supported in the Accept header.")
            val body = response.bufferFactory().wrap(errorMessage.toJson().toByteArray())

            return response.writeWith(Mono.just(body))
        }
        return chain.filter(exchange)    }
}