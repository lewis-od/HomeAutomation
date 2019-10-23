package uk.co.lewisodriscoll.haclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"uk.co.lewisodriscoll.haclient"})
public class HaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaClientApplication.class, args);
	}

}
