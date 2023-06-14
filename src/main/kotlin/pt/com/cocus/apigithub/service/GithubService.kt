package pt.com.cocus.apigithub.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.com.cocus.generated.model.ApiResponseDTO
import pt.com.cocus.generated.model.RepoResponse
import java.util.stream.Stream

@Service
class GithubService(private val webClientService: WebClientService) {

    private suspend fun getAllRepositoryList(authorization: String, username: String): Stream<RepoResponse>? {
        LOGGER.info("[GithubService][INFO][Repository]-Message: Filter repository not Forks.")
        val webClientRepo = webClientService.getAllRepositories(authorization, username)
        return webClientRepo.stream().filter { repo -> !repo.fork!! }
    }

    suspend fun getApiResponse(authorization: String, username: String) : Flow<ApiResponseDTO>? {
        val repoList = getAllRepositoryList(authorization, username)
        var response = mutableListOf<ApiResponseDTO>()
        if (repoList != null) {
            for (repo in repoList) {
                val branchRepo = repo.owner?.login?.let { repo.name?.let { it1 ->  webClientService.getAllBranches(authorization,it,it1) } }
                val apiResponseDTO = ApiResponseDTO(repo.name, repo.owner?.login,branchRepo)
                response.add(apiResponseDTO)
            }
        }
        return response.asFlow()
    }
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GithubService::class.java.name)
    }
}