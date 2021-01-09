package stats.repository;

/**
 * Interface for DynamoDbRepository. These are the available methods that can be used
 * and are implemented in the DynamoDBService.
 */
public interface DynamoDBRepository {

    public void createSongTable();

    public void createArtistTable();

    public String populateSongs() throws Exception;

    public void populateArtists(String[][] artists);

    public void populateSongs(String filename);

}
