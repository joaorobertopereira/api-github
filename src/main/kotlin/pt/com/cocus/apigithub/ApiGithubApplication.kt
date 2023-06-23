package pt.com.cocus.apigithub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
@EnableAsync
@EnableCaching
open class ApiGithubApplication

fun main(args: Array<String>) {
	runApplication<ApiGithubApplication>(*args)
}
