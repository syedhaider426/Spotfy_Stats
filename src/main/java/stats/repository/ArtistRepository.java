package stats.repository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import stats.models.Artist;

import java.util.List;
import java.util.Map;

@EnableScan
public interface ArtistRepository {
    Map<String,String> getAllArtists();

    List<String> getArtists();

    void create(String name, String spotifyId);

    void create(Artist artist);

    void create(List<Artist> artists);

    void update(String name, String spotifyId);

    void update(Artist artist);

    void delete(String name);

    void delete(Artist artist);

}
