import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Artist")
public class Artist {

    private String artist;
    private String spotifyId;
    public Artist() {}

    public Artist(String artist,String spotifyId){
        this.artist = artist;
        this.spotifyId = spotifyId;
    }

    @DynamoDBHashKey(attributeName = "artist")
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @DynamoDBAttribute(attributeName = "spotifyId")
    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    @DynamoDBIgnore
    @Override
    public String toString() {
        return "Artist{" +
                "artist='" + artist + '\'' +
                ", spotifyId='" + spotifyId + '\'' +
                '}';
    }
}
