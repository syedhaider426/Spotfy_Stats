package stats.repository;

import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import org.springframework.stereotype.Repository;
import stats.models.Song;

import java.util.List;

/**
 * Interface for SongRepository. These are the available methods that can be used
 * and are implemented in the SongService.
 */
@Repository
public interface SongRepository {

    boolean isArtistHasSongs(String artist);

    Song create(String artist, String track, String releaseDate, String externalUrl, AudioFeatures audioFeatures);

    void save(List<Song> songs);

    void save(Song song);

    void delete(List<Song> songs);

    void delete(Song song);

}
