package com.puzzly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication
//@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class PuzzlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PuzzlyApplication.class, args);
    }

}
