package com.grabduck.githubsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCaching
@SpringBootApplication
public class GithubRepoSearchApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubRepoSearchApiApplication.class, args);
	}

}
