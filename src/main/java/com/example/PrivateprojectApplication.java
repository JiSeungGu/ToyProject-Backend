package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@ComponentScan({"com.example.domain.aws.repository"})
@SpringBootApplication
public class PrivateprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrivateprojectApplication.class, args);
	}

}
