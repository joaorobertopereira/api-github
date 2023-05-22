package pt.com.cocus.apigithub.adapter

import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "api-github-feign-client", url = "https://api.github.com")
interface GithubAdapter {
}