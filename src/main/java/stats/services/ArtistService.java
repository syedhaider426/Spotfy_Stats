package stats.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stats.exceptions.ConflictException;
import stats.exceptions.NotFoundException;
import stats.exceptions.ServerException;
import stats.models.Artist;
import stats.repository.ArtistRepository;

import java.util.*;

/**
 * Handles the business logic for the Artist model
 */
@Service
public class ArtistService implements ArtistRepository{

    @Autowired
    private DynamoDBMapper mapper;

    /**
     * Get all the artists. This is used in populateSongs of the DynamoDBService.
     * All artists are found and then check to see if they exist in the Songs table.
     * @return map (name,spotifyId) of all the artists found
     */
    public Map<String, String> getAllArtists() {
        Map<String, String> artists = new HashMap<>();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Artist> scanResult = mapper.scan(Artist.class, scanExpression);
        for (Artist artist : scanResult) {
            artists.put(artist.getArtist(), artist.getSpotifyId());
        }
        return artists;
    }

    /**
     * Get all the artists that can be searched for in the autocomplete.
     * Throw error if no artists can be found
     * @return list of artists
     */
    public List<String> getArtists() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Artist> scanResult = mapper.scan(Artist.class, scanExpression);
        List<String> artists = new ArrayList<>();
        if (scanResult.size() == 0)
            throw new ServerException("No artists were found. There was an error with the server.");
        for (Artist artist : scanResult) {
            artists.add(artist.getArtist());
        }
        Collections.sort(artists);
        return artists;
    }

    /**
     * Creates an artist based off name. If artist exists or spotifyId is not found,
     * throw an error
     * @param name of artist
     */
    public void create(String name) {
        Artist artist = mapper.load(Artist.class, name);
        if (artist == null) {
            throw new ConflictException("Artist with name' " + name + "' already exists.");
        }
        String spotifyId = new SpotifyService().searchForArtist(name);
        if (spotifyId.length() == 0)
            throw new NotFoundException("Artist with name '" + name + "' was not found in the Spotify Api.");
        mapper.save(new Artist(name, spotifyId));
    }

    /**
     * Creates an artist from name and spotifyid
     * @param name of artist
     * @param spotifyId of artist
     */
    public void create(String name, String spotifyId) {
        mapper.save(new Artist(name, spotifyId));
    }


    /**
     * Creates one artist
     *
     * @param artist object
     */
    public void create(Artist artist) {
        mapper.save(artist);
    }

    /**
     * Saves list of artists to databases
     *
     * @param artists list<Artist>
     */
    public void create(List<Artist> artists) {
        mapper.batchSave(artists);
    }


    /**
     * Updates an artist in the database
     *
     * @param name      of artist
     * @param spotifyId of artist - field that can be updated
     */
    public void update(String name, String spotifyId) {
        Artist artist = mapper.load(Artist.class, name);
        artist.setArtist(spotifyId);
        mapper.save(artist);
    }

    /**
     * Updates an artist in the database
     *
     * @param artist object
     */
    public void update(Artist artist) {
        mapper.save(artist);
    }

    /**
     * Deletes an artist from the database
     *
     * @param name of artist
     */
    public void delete(String name) {
        Artist artist = mapper.load(Artist.class, name);
        mapper.delete(artist);
    }

    /**
     * Deletes an artist from the database
     *
     * @param artist object
     */
    public void delete(Artist artist) {
        mapper.delete(artist);
    }


}
