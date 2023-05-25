package pt.com.cocus.apigithub.filter

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import pt.com.cocus.apigithub.exceptions.NotAcceptableException
import reactor.core.publisher.Mono


class AppFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        if( exchange.request.headers.accept.contains(MediaType.APPLICATION_XML)) {
            LOGGER.error("[Filter][ERROR]-Message: Could not find acceptable representation")
            throw NotAcceptableException("Could not find acceptable representation")
        }
        return chain.filter(exchange)
    }
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(AppFilter::class.java.name)
    }
}

