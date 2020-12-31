package stats;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        SpringApplication.run(Main.class,args);
    }
}
