package stats.repository;

import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import stats.models.Song;

import java.util.List;

public interface SongRepository {

    boolean getSongsForArtist(String artist);

    Song create(String artist, String track, String releaseDate, String externalUrl, AudioFeatures audioFeatures);

    void save(List<Song> songs);

    void save(Song song);

    void delete(List<Song> songs);

    void delete(Song song);

}
