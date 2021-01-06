package stats.repository;

public interface DynamoDBRepository {

    public void createSongTable();

    public void createArtistTable();

    public String populateSongs();

    public void populateArtists(String[][] artists);

    public void populateSongs(String filename);

}
