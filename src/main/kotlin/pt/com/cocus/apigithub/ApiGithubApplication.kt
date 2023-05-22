package pt.com.cocus.apigithub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class ApiGithubApplication

fun main(args: Array<String>) {
	runApplication<ApiGithubApplication>(*args)
}
