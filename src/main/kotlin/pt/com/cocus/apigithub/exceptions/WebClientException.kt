package pt.com.cocus.apigithub.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class WebClientException(message: String?) : RuntimeException(message)