package pt.com.cocus.apigithub.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.com.cocus.generated.model.ModelApiResponse
import pt.com.cocus.generated.model.RepoResponse
import pt.com.cocus.generated.model.Repository
import java.util.stream.Stream

@Service
class GithubService(private val webClientService: WebClientService) {

    private suspend fun getAllRepositoryList(username: String): Stream<RepoResponse>? {
        LOGGER.info("[GithubService][INFO][Repository]-Message: Filter repository not Forks.")
        val webClientRepo = webClientService.getAllRepositories(username)
        return webClientRepo.stream().filter { repo -> !repo.fork!! }
    }

    suspend fun getApiResponse(username: String): ModelApiResponse? {
        val repoList = getAllRepositoryList(username)
        val repoListResponse = mutableListOf<Repository>()
        if (repoList != null) {
            for (repo in repoList) {
                val branchRepo =
                    repo.owner?.login?.let { repo.name?.let { it1 -> webClientService.getAllBranches(it, it1) } }
                val repository = Repository(repo.name, repo.owner?.login, branchRepo)

                repoListResponse.add(repository)
            }
        }

        return ModelApiResponse(repoListResponse)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GithubService::class.java.name)
    }
}