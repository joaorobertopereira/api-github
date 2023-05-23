package pt.com.cocus.apigithub.dto

import pt.com.cocus.apigithub.model.BranchResponse

data class ApiResponseDTO(
    var name: String?,
    var login: String?,
    var branches: List<BranchResponse>?
)
