package com.assignment.ride_hailing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = DataRedisAutoConfiguration.class)
public class RideHailingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideHailingApplication.class, args);
	}

}
