
package stats.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model for artist
 */
@JsonIgnoreProperties(ignoreUnknown=true)   // When an artist object is created, ignore any unknown properties
@DynamoDBTable(tableName = "Artist")    // Reference the table - Artist
public class Artist {


    private String artist;
    private String spotifyId;

    // Empty constructor
    public Artist() {}

    /**
     * Constructor for Artist
     * @param artist name of artist
     * @param spotifyId spotify uri of artist
     */
    public Artist(String artist, String spotifyId){
        this.artist = artist;
        this.spotifyId = spotifyId;
    }

    /**
     * Gets the artist
     * @return artist name
     */
    @DynamoDBHashKey(attributeName = "artist")
    public String getArtist() {
        return artist;
    }

    /**
     * Sets the artist
     * @param artist name
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Gets the spotify uri for artist
     * @return artist spotifyId
     */
    @DynamoDBAttribute(attributeName = "spotifyId")
    public String getSpotifyId() {
        return spotifyId;
    }

    /**
     * Sets the spotify uri for an artist (uri is received via spotify api)
     * @param spotifyId
     */
    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    /**
     * String representation of Artist model
     * @return printable Artist
     */
    @Override
    public String toString() {
        return "Artist{" +
                "artist='" + artist + '\'' +
                ", spotifyId='" + spotifyId + '\'' +
                '}';
    }
}
