package pt.com.cocus.apigithub.integration.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.springframework.boot.test.context.SpringBootTest
import pt.com.cocus.apigithub.mock.TestConfigs
import pt.com.cocus.generated.model.ApiResponseDTO
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class  ApiGithubControllerTest {

    private lateinit var specification: RequestSpecification
    private lateinit var objectMapper: ObjectMapper
    private lateinit var apiResponse: ApiResponseDTO

    @BeforeAll
    fun setup() {
        objectMapper = ObjectMapper()
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        apiResponse = ApiResponseDTO()

        specification = RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_USERNAME, "joaorobertopereira")
            .setBasePath("/api/github/v1/repositories")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()
    }

    @Test
    @Order(1)
    @Throws(JsonMappingException::class, JsonProcessingException::class)
    fun testFindAll() {
        val strContent = given().spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString()

        val content = objectMapper!!.readValue(strContent, Array<ApiResponseDTO>::class.java)

        val foundApiResponseOne: ApiResponseDTO? = content?.get(0)

        assertNotNull(foundApiResponseOne!!.name)
        assertNotNull(foundApiResponseOne.login)
        assertNotNull(foundApiResponseOne.branches)
        assertEquals("api-ibm-cloud", foundApiResponseOne.name)
        assertEquals("joaorobertopereira", foundApiResponseOne.login)
        assertEquals("main", foundApiResponseOne.branches?.get(0)?.name)
        assertEquals("2297fa719f99879b68a0d3a87bb20d48db00854e", foundApiResponseOne.branches?.get(0)?.commit?.sha)

        val foundApiResponseTwo : ApiResponseDTO? = content?.get(1)

        assertNotNull(foundApiResponseTwo!!.name)
        assertNotNull(foundApiResponseTwo.login)
        assertNotNull(foundApiResponseTwo.branches)
        assertEquals("app-api-github", foundApiResponseTwo.name)
        assertEquals("joaorobertopereira", foundApiResponseTwo.login)
        assertEquals("master", foundApiResponseTwo.branches?.get(0)?.name)
        assertEquals("c9b064c6bfd986bfc30d22832a55df1784bfd920", foundApiResponseTwo.branches?.get(0)?.commit?.sha)

    }

}