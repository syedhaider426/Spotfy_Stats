package stats.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import stats.config.DynamoDBConfiguration;
import stats.models.Artist;
import stats.repository.ArtistRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistService implements ArtistRepository {

    private DynamoDBMapper mapper;

    public ArtistService(){
        mapper = new DynamoDBConfiguration().getMapper();
    }

    public Map<String,String> getAllArtists(){
        Map<String,String> artists = new HashMap<>();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Artist> scanResult = mapper.scan(Artist.class,scanExpression);
        for(Artist artist: scanResult){
            artists.put(artist.getArtist(),artist.getSpotifyId());
        }
        return artists;
    }

    public void create(String name, String spotifyId){
        Artist artist = new Artist(name,spotifyId);
        mapper.save(artist);
    }

    public void create(Artist artist){
        mapper.save(artist);
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
