package org.example.lionhackaton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
public class LionHackatonApplication {

    public static void main(String[] args) {
        SpringApplication.run(LionHackatonApplication.class, args);
    }

}
