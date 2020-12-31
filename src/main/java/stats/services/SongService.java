package stats.services;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import stats.config.DynamoDBConfiguration;
import stats.models.Song;
import stats.repository.SongRepository;

import java.util.List;

public class SongService implements SongRepository {

    private final DynamoDBMapper mapper;

    public SongService(){
        mapper = new DynamoDBConfiguration().getMapper();
    }

    public boolean getSongsForArtist(String artist) {
        final Song song = new Song();
        song.setArtist(artist);
        final DynamoDBQueryExpression<Song> queryExpression = new DynamoDBQueryExpression<Song>()
                .withHashKeyValues(song);
        final PaginatedQueryList<Song> results = mapper.query(Song.class, queryExpression);
        return results.size() == 0;
    }

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

    public void save(List<Song> songs) {
        mapper.batchSave(songs);
        System.out.println("Successfully saved songs!");
    }

    public void save(Song song){
        mapper.save(song);
        System.out.println("Successfully saved song!");
    }

    public void delete(List<Song> songs){
        mapper.batchDelete(songs);
        System.out.println("Successfully deleted song!");
    }

    public void delete(Song song){
        mapper.delete(song);
        System.out.println("Successfully deleted songs!");
    }

}
