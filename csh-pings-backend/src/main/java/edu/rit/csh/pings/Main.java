package edu.rit.csh.pings;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan({"edu.rit.csh.pings.entities"})
@EnableJpaRepositories({"edu.rit.csh.pings.repos"})
@PropertySource("classpath:application.properties")
@PropertySource("classpath:credentials.properties")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
