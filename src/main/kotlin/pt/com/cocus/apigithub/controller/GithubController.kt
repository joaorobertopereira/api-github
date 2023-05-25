package pt.com.cocus.apigithub.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.flow.Flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.com.cocus.apigithub.service.GithubService
import pt.com.cocus.generated.model.ApiResponseDTO
import pt.com.cocus.generated.model.ExceptionResponse
import javax.validation.constraints.Size

@RequestMapping("/api/github/v1")
@RestController
class GithubController(private val service: GithubService) {

    @Operation(
        summary = "get repository list from github",
        operationId = "getAllRepoList",
        description = "repository list",
        responses = [
            ApiResponse(responseCode = "200", description = "OK", content = [Content(schema = Schema(implementation = ApiResponseDTO::class))]),
            ApiResponse(responseCode = "404", description = "Not Found", content = [Content(schema = Schema(implementation = ExceptionResponse::class))]),
            ApiResponse(responseCode = "406", description = "Not Acceptable", content = [Content(schema = Schema(implementation = ExceptionResponse::class))])
        ]
    )
    @RequestMapping(method = [RequestMethod.GET], value = ["/repositories"], produces = ["application/json"])
    suspend fun getAllRepoList(
        @Size(max=256)
        @Parameter(description = "username from github", `in` = ParameterIn.HEADER, required = true)
        @RequestHeader(value = "username", required = true) username: String) : ResponseEntity<Flow<ApiResponseDTO>> {

        LOGGER.info("[GithubController][INFO][getAllRepoList]-Message: Start process to get the repository list")

        return ResponseEntity.ok(service.getApiResponse(username))
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GithubController::class.java.name)
    }
}