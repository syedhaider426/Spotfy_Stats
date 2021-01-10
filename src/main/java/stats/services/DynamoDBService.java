package stats.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Track;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import stats.config.DynamoDBConfiguration;
import stats.models.Song;
import stats.repository.DynamoDBRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Handles the business logic for database. These methods
 * are intended to be used locally (populate data/delete date) or for configuration of the database.
 */
@Service
public class DynamoDBService implements DynamoDBRepository {

    private DynamoDBConfiguration dynamoDBConfiguration;
    private DynamoDB db;
    private DynamoDBMapper mapper;
    private SpotifyService spotify;

    public DynamoDBService() {
        dynamoDBConfiguration = new DynamoDBConfiguration();
        db = dynamoDBConfiguration.getDb();
        mapper = dynamoDBConfiguration.getMapper();
        spotify = new SpotifyService();
    }

    public DynamoDBConfiguration getDynamoDBConfiguration() {
        return dynamoDBConfiguration;
    }

    public DynamoDB getDb() {
        return db;
    }

    public DynamoDBMapper getMapper() {
        return mapper;
    }

    public SpotifyService getSpotify() {
        return spotify;
    }

    // Create the Song table
    public void createSongTable() {
        System.out.println("Attempting to create table; please wait...");
        String tableName = "Song";

        // Atrtibute for keys - artist and releaseDate
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("releaseDate").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("artist").withAttributeType("S"));

        // Key schema for table
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("artist").withKeyType(KeyType.HASH)); // Partition key
        tableKeySchema.add(new KeySchemaElement().withAttributeName("releaseDate").withKeyType(KeyType.RANGE)); // Sort key

        // Initial provisioned throughput settings for the indexes
        ProvisionedThroughput ptIndex = new ProvisionedThroughput()
                .withReadCapacityUnits(5L)
                .withWriteCapacityUnits(5L);

        // Request to create table
        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
                .withProvisionedThroughput(ptIndex)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(tableKeySchema);

        System.out.println("Creating table " + tableName + "...");
        db.createTable(createTableRequest);
        // Wait for table to become active
        System.out.println("Waiting for " + tableName + " to become ACTIVE...");
        try {
            Table table = db.getTable(tableName);
            table.waitForActive();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Create the Artist table
    public void createArtistTable() {
        System.out.println("Attempting to create table; please wait...");
        String tableName = "Artist";

        // Attribute for key
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("artist").withAttributeType("S"));

        // Key schema for table
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("artist").withKeyType(KeyType.HASH)); // Partition key

        // Initial provisioned throughput settings for the indexes
        ProvisionedThroughput ptIndex = new ProvisionedThroughput()
                .withReadCapacityUnits(1L)
                .withWriteCapacityUnits(1L);

        // Request to create table
        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
                .withProvisionedThroughput(ptIndex)
                .withAttributeDefinitions(attributeDefinitions)
                .withStreamSpecification(new StreamSpecification().withStreamEnabled(true).withStreamViewType("KEYS_ONLY"))
                .withKeySchema(tableKeySchema);

        db.createTable(createTableRequest);
        // Wait for table to become active
        System.out.println("Waiting for " + tableName + " to become ACTIVE...");
        try {
            Table table = db.getTable(tableName);
            table.waitForActive();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method used locally to
    public void populateSongs() {
        ArtistService artistService = new ArtistService();
        SongService songService = new SongService();
        Map<String, String> artistsMap = artistService.getAllArtists();
        Map<String, String> artists = new HashMap<>();
        int songCounter = 0;
        for (Map.Entry<String, String> artist : artistsMap.entrySet()) {
            boolean result = songService.isArtistHasSongs(artist.getKey());
            if (!result)
                artists.put(artist.getKey(), artist.getValue());
        }
        for (Map.Entry<String, String> artist : artists.entrySet()) {
            String artistName = artist.getKey();
            String spotifyId = artist.getValue();
            System.out.println("Artist: " + artistName + " - SpotifyID: " + spotifyId);
            List<String> albumReleases = spotify.getReleases(spotifyId);    //batch
            if (albumReleases.size() == 0) {
                System.out.println("No album releases found");
                break;
            }
            List<String> tracks = spotify.getAlbumTracks(artistName, albumReleases);    //not batch
            if (tracks.size() == 0) {
                System.out.println("No tracks found");
                break;
            }
            List<AudioFeatures> audioFeatureList = spotify.getSeveralTrackFeatures(tracks); //batch
            if (audioFeatureList.size() == 0) {
                System.out.println("No audio features found");
                break;
            }
            List<String> trackList = new ArrayList<>();
            for (AudioFeatures track : audioFeatureList) {
                trackList.add(track.getId());
            }
            List<Track> songInfoList = spotify.getSongInfo(trackList);  //batch
            if (songInfoList.size() == 0) {
                System.out.println("No song information found");
                break;
            }
            int x = 0;
            List<Song> songs = new ArrayList<>();
            for (AudioFeatures audioFeature : audioFeatureList) {
                String name = songInfoList.get(x).getName();
                String releaseDate = songInfoList.get(x).getAlbum().getReleaseDate();
                String externalUrl = songInfoList.get(x).getExternalUrls().get("spotify");
                Song newSong = songService.create(artistName, name, releaseDate, externalUrl, audioFeature);
                songs.add(newSong);
                x++;
            }
            songCounter += songs.size();
            songService.save(songs);
        }
        if (artists.size() == 0)
            System.out.println("") ;
        else
            System.out.println("Artists - " + artists.size() + "Songs - " + songCounter);
    }

    /**
     * Method used by the Lambda function to get the songs and the song attributes for a specific artist
     * @param name of artist
     * @param spotifyId of artist
     * @return success/failure message
     */
    public String populateSongs(String name, String spotifyId) {
        SongService songService = new SongService();

        // Get albums created by artist - albums can represent a one or more songs
        List<String> albumReleases = spotify.getReleases(spotifyId);
        if (albumReleases.size() == 0) {
            return "No album releases found";
        }

        // Get tracks for each album
        List<String> tracks = spotify.getAlbumTracks(name, albumReleases);
        if (tracks.size() == 0) {
            return "No tracks found";
        }

        // Get audio features of each song
        List<AudioFeatures> audioFeatureList = spotify.getSeveralTrackFeatures(tracks);
        if (audioFeatureList.size() == 0) {
            return "No audio features found";
        }
        List<String> trackList = new ArrayList<>();

        /**
         * A new list of tracks is returned from getSeveralTrackFeatures because
         * some songs do not have audio features
         **/
        for (AudioFeatures track : audioFeatureList) {
            trackList.add(track.getId());
        }

        // Get song name, releaseDate, and external url (the playable Spotify link)
        List<Track> songInfoList = spotify.getSongInfo(trackList);  //batch
        if (songInfoList.size() == 0) {
            return "No song information found";
        }
        int x = 0;
        List<Song> songs = new ArrayList<>();
        for (AudioFeatures audioFeature : audioFeatureList) {
            String songName = songInfoList.get(x).getName();
            String releaseDate = songInfoList.get(x).getAlbum().getReleaseDate();
            String externalUrl = songInfoList.get(x).getExternalUrls().get("spotify");
            Song newSong = songService.create(name, songName, releaseDate, externalUrl, audioFeature);
            songs.add(newSong);
            x++;
        }
        songService.save(songs);
        if (songs.size() == 0)
            return "No songs added";
        return "Artist and Songs added";
    }


    // Method used locally to load artists in from a 2D-array of artist/spotifyId
    public void populateArtists(String[][] artists) {
        ArtistService a = new ArtistService();
        for (int x = 0; x < artists.length; x++) {
            a.create(artists[x][0], artists[x][1]);
        }
    }

    // Method used locally to load songs of an artist in database from a file
    public void populateSongs(String filename) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        float loudness, liveness, tempo, acousticness, energy, valence, instrumentalness, danceability, speechiness;
        JSONObject obj, jsonobject;
        String song, artist, releaseDate, link, spotifyId;
        Set<Song> songs = new HashSet<>();
        try (FileReader reader = new FileReader(filename)) {
            //Read JSON file and return JSONObject
            obj = (JSONObject) jsonParser.parse(reader);
            //Get array of items
            JSONArray jsonArray = (JSONArray) obj.get("Items");
            int counter = 1;
            for (Object o : jsonArray) {
                // Get JSONobject from array
                jsonobject = (JSONObject) o;

                // Get fields from file
                song = (String) ((JSONObject) jsonobject.get("song")).get("S");
                artist = (String) ((JSONObject) jsonobject.get("artist")).get("S");
                releaseDate = (String) ((JSONObject) jsonobject.get("releaseDate")).get("S");
                link = (String) ((JSONObject) jsonobject.get("link")).get("S");
                spotifyId = (String) ((JSONObject) jsonobject.get("spotifyId")).get("S");
                int val = Integer.parseInt((String) ((JSONObject) jsonobject.get("hidden")).get("N"));
                boolean hidden = val == 1;
                loudness = Float.parseFloat((String) ((JSONObject) jsonobject.get("loudness")).get("N"));
                liveness = Float.parseFloat((String) ((JSONObject) jsonobject.get("liveness")).get("N"));
                tempo = Float.parseFloat((String) ((JSONObject) jsonobject.get("tempo")).get("N"));
                acousticness = Float.parseFloat((String) ((JSONObject) jsonobject.get("acousticness")).get("N"));
                valence = Float.parseFloat((String) ((JSONObject) jsonobject.get("valence")).get("N"));
                instrumentalness = Float.parseFloat((String) ((JSONObject) jsonobject.get("instrumentalness")).get("N"));
                danceability = Float.parseFloat((String) ((JSONObject) jsonobject.get("danceability")).get("N"));
                speechiness = Float.parseFloat((String) ((JSONObject) jsonobject.get("speechiness")).get("N"));
                energy = Float.parseFloat((String) ((JSONObject) jsonobject.get("energy")).get("N"));

                //Create song
                Song newSong = new Song();
                newSong.setAcousticness(acousticness);
                newSong.setArtist(artist);
                newSong.setDanceability(danceability);
                newSong.setEnergy(energy);
                newSong.setInstrumentalness(instrumentalness);
                newSong.setLink(link);
                newSong.setLiveness(liveness);
                newSong.setLoudness(loudness);
                newSong.setReleaseDate(releaseDate);
                newSong.setSong(song);
                newSong.setSpeechiness(speechiness);
                newSong.setTempo(tempo);
                newSong.setId(spotifyId);
                newSong.setValence(valence);
                newSong.setHidden(hidden);
                songs.add(newSong);
                System.out.println("Added - " + counter);
                counter++;
            }
            System.out.println("Going through the songs");
            mapper.batchSave(songs);
            System.out.println("Completed");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}
