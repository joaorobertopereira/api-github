package pt.com.cocus.apigithub.model

data class RepoResponse(
    val name: String,
    val owner: Owner,
    val fork: Boolean
)