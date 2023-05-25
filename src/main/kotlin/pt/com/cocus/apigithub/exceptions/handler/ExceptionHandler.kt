package pt.com.cocus.apigithub.exceptions.handler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebExchange
import pt.com.cocus.apigithub.exceptions.ExceptionResponse
import pt.com.cocus.apigithub.exceptions.NotAcceptableException

@ControllerAdvice
class ExceptionHandler  {

    @ExceptionHandler
    fun handleWebClientResponseNotFoundExceptions(ex: Exception, exchange: ServerWebExchange) :
            ResponseEntity<ExceptionResponse> {

        val exceptioResponse = ExceptionResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )
        LOGGER.error("[Handler][NOT-FOUND]-Message: Not Found repository with this username: {}",
            exchange.request.headers["username"]
        )
        return ResponseEntity<ExceptionResponse>(exceptioResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(NotAcceptableException::class)
    fun handleNotAcceptableExceptions(ex: NotAcceptableException, exchange: ServerWebExchange) : ResponseEntity<ExceptionResponse> {
        val exceptioResponse = ExceptionResponse(
            HttpStatus.NOT_ACCEPTABLE.value(),
            ex.message
        )
        LOGGER.error("[Handler][NOT-ACCEPTABLE]-Message: Not Acceptable Header: {}",
            exchange.request.headers.accept
        )
        return ResponseEntity<ExceptionResponse>(exceptioResponse, HttpStatus.NOT_ACCEPTABLE)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java.name)
    }
}