package pt.com.cocus.apigithub.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import pt.com.cocus.apigithub.model.BranchResponse
import pt.com.cocus.apigithub.model.RepoResponse


@Service
class WebClientService(private val webClient: WebClient) {

    suspend fun getAllRepositories(username: String, auth: String) : List<RepoResponse> {
        LOGGER.info("Get a List of repositories from : $username")
        return webClient.get()
                .uri("/users/$username/repos")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", auth)
                .retrieve()
                .awaitBody<List<RepoResponse>>()
    }


    suspend fun getAllBranches(username: String, repo: String, auth: String) : List<BranchResponse> {
        LOGGER.info("Get a List of branches from repo : $repo")
        return webClient.get()
                .uri("/repos/$username/$repo/branches")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",auth)
                .retrieve()
                .awaitBody<List<BranchResponse>>()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(WebClientService::class.java.name)
    }
}

