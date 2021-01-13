package stats.repository;

import org.springframework.stereotype.Repository;
import stats.models.Artist;

import java.util.List;
import java.util.Map;

/**
 * Interface for ArtistRepository. These are the available methods that can be used
 * and are implemented in the ArtistService.
 */
@Repository
public interface ArtistRepository {
    Map<String,String> getAllArtists() throws Exception;

    List<String> getArtists();

    void create(String name);

    void create(Artist artist);

    void create(List<Artist> artists);

    void create(String name, String spotifyId);

    void update(String name, String spotifyId);

    void update(Artist artist);

    void delete(String name);

    void delete(Artist artist);

}
