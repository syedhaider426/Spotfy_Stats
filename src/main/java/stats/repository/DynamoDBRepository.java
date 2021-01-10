package stats.repository;

import org.springframework.stereotype.Repository;

/**
 * Interface for DynamoDbRepository. These are the available methods that can be used
 * and are implemented in the DynamoDBService.
 */
@Repository
public interface DynamoDBRepository {

    public void createSongTable();

    public void createArtistTable();

    public void populateSongs();

    public String populateSongs(String name, String spotifyId);

    public void populateArtists(String[][] artists);

    public void populateSongs(String filename);

}
