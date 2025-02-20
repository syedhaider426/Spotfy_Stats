package stats;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Entry point for Spring Boot Application. This application can return song stats for a specific artist.
 * Users can also add an artist to the database.
 */
@SpringBootApplication
@ComponentScan(basePackages={"stats"})
public class Main {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }


}
