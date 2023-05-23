package pt.com.cocus.apigithub.service

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import pt.com.cocus.apigithub.model.BranchResponse
import pt.com.cocus.apigithub.model.RepoResponse
import java.util.logging.Logger

@Service
class WebClientService(private val webClient: WebClient) {
    private val logger = Logger.getLogger(WebClientService::class.java.name)
    suspend fun getAllRepositories(username: String, auth: String) : List<RepoResponse> {
        logger.info("Get a List of repositories from : $username")
        return webClient.get()
                .uri("/users/$username/repos")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", auth)
                .retrieve()
                .awaitBody<List<RepoResponse>>()
    }


    suspend fun getAllBranches(username: String, repo: String, auth: String) : List<BranchResponse> {
        logger.info("Get a List of branches from repo : $repo")
        return webClient.get()
                .uri("/repos/$username/$repo/branches")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",auth)
                .retrieve()
                .awaitBody<List<BranchResponse>>()
    }

}

