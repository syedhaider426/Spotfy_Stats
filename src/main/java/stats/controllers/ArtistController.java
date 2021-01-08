package stats.controllers;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import stats.models.Artist;
import stats.services.ArtistService;
import stats.services.SpotifyService;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class ArtistController {

    private ArtistService artistService;

    public ArtistController(){
        artistService = new ArtistService();
    }

    @GetMapping(value = "/allArtists")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<String> getArtists(){
        List<String> artists = artistService.getArtists();
        return artists;
    }

    @PostMapping(path = "/artist",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createArtist(@RequestBody Artist artist) {
        artistService.create(artist.getArtist());
    }

}
