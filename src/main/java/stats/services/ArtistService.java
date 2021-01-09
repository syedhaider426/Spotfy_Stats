package stats.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.springframework.stereotype.Service;
import stats.config.DynamoDBConfiguration;
import stats.exceptions.ConflictException;
import stats.exceptions.NotFoundException;
import stats.exceptions.ServerException;
import stats.models.Artist;
import stats.repository.ArtistRepository;

import java.util.*;

@Service
public class ArtistService implements ArtistRepository {

    private DynamoDBMapper mapper;


    public ArtistService(){
        mapper = new DynamoDBConfiguration().getMapper();
    }

    // Used to load in data
    public Map<String,String> getAllArtists(){
        Map<String,String> artists = new HashMap<>();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Artist> scanResult = mapper.scan(Artist.class,scanExpression);
        for(Artist artist: scanResult){
            artists.put(artist.getArtist(),artist.getSpotifyId());
        }
        return artists;
    }

    // Used to populate autocomplete
    public List<String> getArtists(){
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Artist> scanResult = mapper.scan(Artist.class,scanExpression);
        List<String> artists = new ArrayList<>();
        if(artists.size() == 0)
            throw new ServerException("No artists were found. There was an error with the server.");
        for(Artist artist: scanResult){
            artists.add(artist.getArtist());
        }
        Collections.sort(artists);
        return artists;
    }


    public boolean getArtist(String name){
        Artist artist = mapper.load(Artist.class,name);
        if(artist == null)
            return false;
        return true;
    }

    public void create(String name, String spotifyId){
        mapper.save(new Artist(name,spotifyId));
    }

    public void create(String name){
        boolean result = getArtist(name);
        if(result) {
            throw new ConflictException("Artist with name' " + name + "' already exists.");
        }
        String spotifyId = new SpotifyService().searchForArtist(name);
        if(spotifyId.length() == 0)
            throw new NotFoundException("Artist with name '" + name + "' was not found in the Spotify Api.");
        mapper.save(new Artist(name,spotifyId));
    }


    public void create(Artist artist){
        mapper.save(artist);
    }

    public void create(List<Artist> artist){
        mapper.batchSave(artist);
    }

    public void update(String name, String spotifyId){
        Artist artist = mapper.load(Artist.class,name);
        artist.setArtist(spotifyId);
        mapper.save(artist);
    }

    public void update(Artist artist){
        mapper.save(artist);
    }

    public void delete(String name){
        Artist artist = mapper.load(Artist.class,name);
        mapper.delete(artist);
    }

    public void delete(Artist artist){
        mapper.delete(artist);
    }




}
