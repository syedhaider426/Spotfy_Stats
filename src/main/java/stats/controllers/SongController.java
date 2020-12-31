package stats.controllers;

import org.springframework.web.bind.annotation.*;
import stats.Database;

import java.util.Collections;
import java.util.Set;

@RestController
public class SongController {

    @CrossOrigin
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Set<String> getSongs(@RequestParam("artist") String artist){
        System.out.println(artist);
        new Database().loadData();
        return Collections.singleton(artist);
    }

}
