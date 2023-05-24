package pt.com.cocus.apigithub.filter

import org.springframework.http.MediaType
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


class AppFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        if( exchange.request.headers.accept.contains(MediaType.APPLICATION_XML)) {
            throw NotAcceptableStatusException("Acceptable representations: [application/json, application/*+json, application/x-ndjson, text/event-stream]")
        }
        return chain.filter(exchange)
    }

}

