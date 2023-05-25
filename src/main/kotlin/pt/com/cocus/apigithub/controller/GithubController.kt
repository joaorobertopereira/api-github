package pt.com.cocus.apigithub.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.com.cocus.apigithub.dto.ApiResponseDTO
import pt.com.cocus.apigithub.service.GithubService

@RestController
@RequestMapping("/api/github/v1")
class GithubController(private val service: GithubService) {

    @GetMapping("/{username}")
    suspend fun getAllRepoList(
        @PathVariable("username") username: String,
        @RequestHeader("Authorization") auth : String
    ): ResponseEntity<List<ApiResponseDTO>> {
        LOGGER.info("[GithubController][INFO][getAllRepoList]-Message: Start process to get the repository list")
        return ResponseEntity.ok(service.getApiResponse(username, auth))
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GithubController::class.java.name)
    }
}