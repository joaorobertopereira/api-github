package pt.com.cocus.apigithub.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.com.cocus.apigithub.service.GithubService
import pt.com.cocus.generated.api.RepositoriesApi
import pt.com.cocus.generated.model.ModelApiResponse

@RequestMapping("/api/github/v1")
@RestController
class GithubController(private val service: GithubService) : RepositoriesApi {
    override suspend fun getAllRepoList(username: String): ResponseEntity<ModelApiResponse> {
        LOGGER.info("[GithubController][INFO][getAllRepoList]-Message: Start process to get the repository list")

        return ResponseEntity.ok(service.getApiResponse(username))
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GithubController::class.java.name)
    }
}