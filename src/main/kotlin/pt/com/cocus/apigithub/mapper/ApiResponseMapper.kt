package pt.com.cocus.apigithub.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import pt.com.cocus.apigithub.dto.ApiResponseDTO
import pt.com.cocus.apigithub.model.RepoResponse

@Mapper
interface ApiResponseMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "owner.login", target = "login")
    fun toDto(repoResponse: RepoResponse): ApiResponseDTO

}