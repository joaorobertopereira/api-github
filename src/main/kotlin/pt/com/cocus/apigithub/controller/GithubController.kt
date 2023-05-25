package pt.com.cocus.apigithub.controller

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
        return ResponseEntity.ok(service.getApiResponse(username, auth))
    }

}