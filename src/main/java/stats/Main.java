package stats;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import stats.services.SongService;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        new SongService().createFile();
        //SpringApplication.run(Main.class,args);
    }
}
