package pt.com.cocus.apigithub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiGithubApplication

fun main(args: Array<String>) {
	runApplication<ApiGithubApplication>(*args)
}
