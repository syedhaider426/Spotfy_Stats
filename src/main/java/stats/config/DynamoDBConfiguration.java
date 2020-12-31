package stats.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Track;
import stats.models.Song;
import stats.services.ArtistService;
import stats.services.SongService;
import stats.services.SpotifyService;

import java.util.*;
import java.util.stream.Collectors;

public class DynamoDBConfiguration {
    private final DynamoDB db;
    private final DynamoDBMapper mapper;
    private final AmazonDynamoDB client;
    private final SpotifyService spotify;

    public DynamoDBConfiguration(){
        client = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();
        this.mapper = new DynamoDBMapper(client);
        this.db = new DynamoDB(client);
        spotify = new SpotifyService();
    }

    public DynamoDB getDb() {
        return db;
    }
    public DynamoDBMapper getMapper() {
        return mapper;
    }
    public AmazonDynamoDB getClient() {
        return client;
    }


    public void createSongTable(){
        System.out.println("Attempting to create table; please wait...");
        String tableName = "Song";

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("releaseDate").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("artist").withAttributeType("S"));

        // Key schema for table
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("artist").withKeyType(KeyType.HASH)); // Partition key
        tableKeySchema.add(new KeySchemaElement().withAttributeName("releaseDate").withKeyType(KeyType.RANGE)); // Sort key

        // Initial provisioned throughput settings for the indexes
        ProvisionedThroughput ptIndex = new ProvisionedThroughput()
                .withReadCapacityUnits(1L)
                .withWriteCapacityUnits(1L);

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
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createArtistTable() {
        try {
            System.out.println("Attempting to create table; please wait...");
            String tableName = "Artist";
            Table table = db.createTable(tableName,
                    Collections.singletonList(new KeySchemaElement("artist", KeyType.HASH)),
                    Collections.singletonList(new AttributeDefinition("artist", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());
        } catch (ResourceInUseException ex) {
            System.out.println("Table already exists");
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Unable to create table: ");
            ex.printStackTrace();
        }

    }

    public void loadData() {
        ArtistService artistService = new ArtistService();
        SongService songService = new SongService();
        Map<String, String> artistsMap = artistService.getAllArtists();
        Map<String, String> artists = new HashMap<>();
        for (Map.Entry<String, String> artist : artistsMap.entrySet()) {
            boolean result = songService.getSongsForArtist(artist.getKey());
            if (result)
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
                String externalUrl = songInfoList.get(x).getAlbum().getExternalUrls().get("spotify");
                Song newSong = songService.create(artistName, name, releaseDate, externalUrl, audioFeature);
                songs.add(newSong);
                x++;
            }
            songService.save(songs);
        }
        if(artists.size() == 0)
            System.out.println("No more songs to save");
    }










}
