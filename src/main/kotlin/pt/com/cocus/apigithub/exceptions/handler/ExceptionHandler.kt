package pt.com.cocus.apigithub.exceptions.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.NotAcceptableStatusException
import pt.com.cocus.apigithub.exceptions.ExceptionResponse

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler
    fun handleWebClientResponseNotFoundExceptions(ex: Exception) :
            ResponseEntity<ExceptionResponse> {
        val exceptioResponse = ExceptionResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )
        return ResponseEntity<ExceptionResponse>(exceptioResponse, HttpStatus.NOT_FOUND)
    }
    @ExceptionHandler
    fun handleNotAcceptibleExceptions(ex: NotAcceptableStatusException) : ResponseEntity<ExceptionResponse> {
        val exceptioResponse = ExceptionResponse(
            HttpStatus.NOT_ACCEPTABLE.value(),
            ex.message
        )
        return ResponseEntity<ExceptionResponse>(exceptioResponse, HttpStatus.NOT_ACCEPTABLE)
    }
}