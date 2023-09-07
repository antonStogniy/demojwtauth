package com.example.demojwtauth;

import com.example.demojwtauth.entity.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class DemojwtauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemojwtauthApplication.class, args);
	}

}
