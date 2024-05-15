package com.puzzly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan({"com.puzzly.api.entity", "com.puzzly.api.domain", "com.puzzly.api.dto"})
@SpringBootApplication
//@SpringBootApplication
//@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class PuzzlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PuzzlyApplication.class, args);
    }

}
