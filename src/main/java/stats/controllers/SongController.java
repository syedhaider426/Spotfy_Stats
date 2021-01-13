package stats.controllers;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import stats.models.Song;
import stats.repository.SongRepository;
import stats.services.SongService;


/**
 * Handles all the requests to any endpoints related to Songs
 */
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class SongController {

    @Autowired
    private SongRepository songRepository;

    /**
     * Gets all the songs and related info for songs for a specific artist
     * @param artist name of artists
     * @return list of songs and their attributes
     */
    @GetMapping(value = "/info")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedQueryList<Song> getSongs(@RequestParam("artist") String artist){
        PaginatedQueryList<Song> results = songRepository.getSongsForArtist(artist);
        return results;
    }

}
