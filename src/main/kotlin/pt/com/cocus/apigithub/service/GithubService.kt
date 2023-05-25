package pt.com.cocus.apigithub.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.com.cocus.apigithub.dto.ApiResponseDTO
import pt.com.cocus.apigithub.model.RepoResponse
import java.util.stream.Stream

@Service
class GithubService(private val webClientService: WebClientService) {

    private suspend fun getAllRepositoryList(username: String, auth: String): Stream<RepoResponse>? {
        LOGGER.info("[GithubService][INFO][Repository]-Message: Filter repository not Forks.")
        val webClientRepo = webClientService.getAllRepositories(username, auth)
        return webClientRepo.stream().filter { repo -> !repo.fork }
    }

    suspend fun getApiResponse(username: String, auth: String) : List<ApiResponseDTO>? {
        val repoList = getAllRepositoryList(username, auth)
        var response = mutableListOf<ApiResponseDTO>()
        if (repoList != null) {
            for (repo in repoList) {
                val branchRepo = webClientService.getAllBranches(repo.owner.login, repo.name, auth)
                val apiResponseDTO = ApiResponseDTO(null,null,null)
                apiResponseDTO.name = repo.name
                apiResponseDTO.login = repo.owner.login
                apiResponseDTO.branches = branchRepo
                response.add(apiResponseDTO)
            }
        }
        return response
    }
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GithubService::class.java.name)
    }
}