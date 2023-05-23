package pt.com.cocus.apigithub.model

data class BranchResponse(
    val name: String,
    val commit: Commit
)