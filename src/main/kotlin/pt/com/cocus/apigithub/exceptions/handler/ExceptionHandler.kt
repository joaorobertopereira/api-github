package pt.com.cocus.apigithub.exceptions.handler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import pt.com.cocus.apigithub.exceptions.ExceptionResponse

@ControllerAdvice
class ExceptionHandler  {

    @ExceptionHandler
    fun handleWebClientExceptions(ex: Exception, exchange: ServerWebExchange):
            ResponseEntity<ExceptionResponse> {

        val status = when (ex) {
            is ServerWebInputException -> ex.status
            is WebClientResponseException -> ex.statusCode
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        val exceptionResponse = ExceptionResponse(
            status.value(),
            ex.message
        )

        val logMessage = when (status) {
            HttpStatus.NOT_FOUND -> "[Handler][NOT-FOUND]-Message: Not Found repository with this username: ${exchange.request.headers["username"]}"
            HttpStatus.BAD_REQUEST -> "[Handler][BAD-REQUEST]-Message: Bad Request: ${ex.message}"
            HttpStatus.INTERNAL_SERVER_ERROR -> "[Handler][INTERNAL-SERVER-ERROR]-Message: Internal Server Error: ${ex.message}"
            else -> "[Handler][UNKNOWN-ERROR]-Message: Unknown error occurred: ${ex.message}"
        }

        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            LOGGER.error(logMessage, ex)
        } else {
            LOGGER.error(logMessage)
        }

        return ResponseEntity(exceptionResponse, status)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java.name)
    }
}