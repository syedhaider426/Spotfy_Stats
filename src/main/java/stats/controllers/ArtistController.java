package stats.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import stats.models.Artist;
import stats.repository.ArtistRepository;

import java.util.List;

/**
 * Handles all the requests to any endpoints related to Artists
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;

    /**
     * Gets all the available artists to search in the autocomplete feature
     *
     * @return
     */
    @GetMapping(value = "/api/allArtists")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getArtists() {
        List<String> artists = artistRepository.getArtists();
        return artists;
    }

    /**
     * Post request that creates an artist
     *
     * @param artist name of artist
     */
    @PostMapping(path = "/api/artist",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createArtist(@RequestBody Artist artist) {
        artistRepository.create(artist.getArtist());
    }

}
