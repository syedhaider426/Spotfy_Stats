package stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import stats.config.DynamoDBConfiguration;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        new DynamoDBConfiguration();
        //SpringApplication.run(Main.class,args);
    }
}
