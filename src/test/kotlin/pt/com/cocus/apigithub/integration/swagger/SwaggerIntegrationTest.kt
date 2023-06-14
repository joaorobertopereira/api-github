package pt.com.cocus.apigithub.integration.swagger

import io.restassured.RestAssured.given
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import pt.com.cocus.apigithub.mock.TestConfigs

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SwaggerIntegrationTest()  {

	@Test
	fun shouldDisplaySwaggerUiPage() {
		val content = given()
			.basePath("/swagger-ui.html")
			.port(TestConfigs.SERVER_PORT)
				.`when`()
			.get()
			.then()
				.statusCode(200)
			.extract()
			.body()
				.asString()
		assertTrue(content.contains("Swagger UI"))
	}

}