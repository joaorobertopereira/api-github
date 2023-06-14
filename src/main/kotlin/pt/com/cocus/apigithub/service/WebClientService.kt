package pt.com.cocus.apigithub.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import pt.com.cocus.generated.model.BranchResponse
import pt.com.cocus.generated.model.RepoResponse


@Service
class WebClientService(private val webClient: WebClient) {

    suspend fun getAllRepositories(authorization: String, username: String) : List<RepoResponse> {
        LOGGER.info("[WebClient][GET][Repositories]-Message: Get a List of repositories from : $username")
        return webClient.get()
                .uri("/users/$username/repos")
                .headers { createAuthHeader(authorization) }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody<List<RepoResponse>>()
    }

    suspend fun getAllBranches(authorization: String, username: String, repo: String) : List<BranchResponse> {
        LOGGER.info("[WebClient][GET][Branch]-Message: Get a List of branches from repository : $repo")
        return webClient.get()
                .uri("/repos/$username/$repo/branches")
                .headers { createAuthHeader(authorization) }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody<List<BranchResponse>>()
    }

    private fun createAuthHeader(authorization: String): HttpHeaders {
        var authHeader = HttpHeaders()

        if (authorization.isNullOrEmpty()) {
            authHeader.add("Authorization", "Bearer $authorization")
        }
        return authHeader
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(WebClientService::class.java.name)
    }
}

