package stats.services;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import org.springframework.stereotype.Service;
import stats.config.DynamoDBConfiguration;
import stats.exceptions.ServerException;
import stats.models.Song;
import stats.repository.SongRepository;

import java.util.List;

/**
 * Handles all the business logic for the Song model
 */
@Service
public class SongService implements SongRepository {

    private final DynamoDBMapper mapper;

    public SongService(){
        mapper = new DynamoDBConfiguration().getMapper();
    }

    /**
     * Queries for the songs the artist has created.
     * @param artist name of artist
     * @return list of songs the Artist has created
     */
    public PaginatedQueryList<Song> getSongsForArtist(String artist){
        final Song song = new Song();
        song.setArtist(artist);
        final DynamoDBQueryExpression<Song> queryExpression = new DynamoDBQueryExpression<Song>()
                .withHashKeyValues(song);
        final PaginatedQueryList<Song> results = mapper.query(Song.class, queryExpression);
        if(results.size() == 0)
            throw new ServerException("No songs found for " + artist);
        return results;
    }

    /**
     * Determines whether or not the Song table has a track with specific artist
     * @param artist name of artist
     * @return true if artist has songs, false if not
     */
    public boolean isArtistHasSongs(String artist) {
        return getSongsForArtist(artist).size() > 0;
    }

    // Create a song based off the provided parameters
    public Song create(String artist, String track, String releaseDate, String externalUrl, AudioFeatures audioFeatures) {
        Song song = new Song();
        song.setAcousticness(audioFeatures.getAcousticness());
        song.setArtist(artist);
        song.setDanceability(audioFeatures.getDanceability());
        song.setEnergy(audioFeatures.getEnergy());
        song.setInstrumentalness(audioFeatures.getInstrumentalness());
        song.setLink(externalUrl);
        song.setLiveness(audioFeatures.getLiveness());
        song.setLoudness(audioFeatures.getLoudness());
        song.setReleaseDate(releaseDate + " -- " + artist + " -- " + audioFeatures.getId());
        song.setSong(track);
        song.setSpeechiness(audioFeatures.getSpeechiness());
        song.setTempo(audioFeatures.getTempo());
        song.setId(audioFeatures.getId());
        song.setValence(audioFeatures.getValence());
        song.setHidden(false);
        return song;
    }

    /**
     * Insert a list of songs
     * @param songs list of songs to insert
     */
    public void save(List<Song> songs) {
        mapper.batchSave(songs);
        System.out.println("Successfully saved songs!");
    }

    /**
     * Insert a song to the database
     * @param song to insert
     */
    public void save(Song song){
        mapper.save(song);
        System.out.println("Successfully saved song!");
    }

    /**
     * Delete a list of songs
     * @param songs list of songs to delete
     */
    public void delete(List<Song> songs){
        mapper.batchDelete(songs);
        System.out.println("Successfully deleted song!");
    }

    /**
     * Delete a specific song
     * @param song to delete
     */
    public void delete(Song song){
        mapper.delete(song);
        System.out.println("Successfully deleted songs!");
    }

    // method used locally to hide and delete tracks
    public void hide(){
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Song> scanResult = mapper.scan(Song.class,scanExpression);
        System.out.println(scanResult.size());
        for(Song s: scanResult){
            String date = s.getReleaseDate().substring(0,s.getReleaseDate().indexOf("--")-1);
            if(Integer.parseInt(date.substring(0,4)) <= 2000){
                s.setHidden(true);
                mapper.save(s);
            }
        }
        DynamoDBScanExpression scanExpression2 = new DynamoDBScanExpression();
        List<Song> scanResult2 = mapper.scan(Song.class,scanExpression2);
        for (Song song : scanResult2) {
            if(song.isHidden())
                mapper.delete(song);
        }
    }




}
