package stats.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import stats.services.ArtistService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class ArtistController {

    private ArtistService artistService;

    public ArtistController(){
        artistService = new ArtistService();
    }

    @RequestMapping(value = "/allArtists", method = RequestMethod.GET)
    public List<String> getArtists(){
        List<String> artists = artistService.getArtists();
        return artists;
    }

}
