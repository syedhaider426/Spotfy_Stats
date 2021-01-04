package stats.repository;
import stats.models.Artist;

import java.util.List;
import java.util.Map;

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
