package stats.controllers;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import stats.models.Song;
import stats.services.SongService;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class SongController {

    private SongService songService;

    @GetMapping(value = "/info")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PaginatedQueryList<Song> getSongs(@RequestParam("artist") String artist){
        PaginatedQueryList<Song> results = songService.getSongsForArtist(artist);
        return results;
    }

}
