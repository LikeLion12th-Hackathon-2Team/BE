package org.example.lionhackaton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LionHackatonApplication {

    public static void main(String[] args) {
        SpringApplication.run(LionHackatonApplication.class, args);
    }

}
