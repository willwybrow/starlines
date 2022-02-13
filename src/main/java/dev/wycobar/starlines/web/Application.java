package dev.wycobar.starlines.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@SpringBootApplication
@ComponentScan(basePackages = "dev.wycobar.starlines")
@EnableScheduling
@Configuration
public class Application {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
