package stats.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import org.springframework.web.bind.annotation.*;
import stats.Database;
import stats.models.Song;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface SongRepository {

    boolean getSongsForArtist(String artist);

    Song create(String artist, String track, String releaseDate, String externalUrl, AudioFeatures audioFeatures);

    void save(List<Song> songs);

    void save(Song song);

    void delete(List<Song> songs);

    void delete(Song song);

}
