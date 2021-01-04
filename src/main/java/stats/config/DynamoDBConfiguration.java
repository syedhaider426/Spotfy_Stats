package stats.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Track;
import stats.models.Artist;
import stats.models.Song;
import stats.services.ArtistService;
import stats.services.SongService;
import stats.services.SpotifyService;

import java.io.IOException;
import java.util.*;

public class DynamoDBConfiguration {
    private DynamoDB db;
    private DynamoDBMapper mapper;
    private AmazonDynamoDB client;
    private SpotifyService spotify;

    public DynamoDBConfiguration(){
        try {
            GetPropertyValues properties = new GetPropertyValues();
            Properties prop = properties.getPropValues();
        client = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();
//            client = AmazonDynamoDBClientBuilder
//                    .standard()
//                    .withRegion("us-west-2")
//                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(prop.getProperty("accessKey"), prop.getProperty("secretKey"))))
//                    .build();
            this.mapper = new DynamoDBMapper(client);
            this.db = new DynamoDB(client);
            spotify = new SpotifyService();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listTables(){
        System.out.println(mapper.load(Artist.class,"Crizzly").getArtist());
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
            System.out.println("Attempting to create table; please wait...");
            String tableName = "Artist";

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("artist").withAttributeType("S"));

            // Key schema for table
            ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
            tableKeySchema.add(new KeySchemaElement().withAttributeName("artist").withKeyType(KeyType.HASH)); // Partition key

            // Initial provisioned throughput settings for the indexes
            ProvisionedThroughput ptIndex = new ProvisionedThroughput()
                    .withReadCapacityUnits(1L)
                    .withWriteCapacityUnits(1L);

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
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public String loadData() {
        ArtistService artistService = new ArtistService();
        SongService songService = new SongService();
        Map<String, String> artistsMap = artistService.getAllArtists();
        Map<String, String> artists = new HashMap<>();
        int songCounter = 0;
        for (Map.Entry<String, String> artist : artistsMap.entrySet()) {
            boolean result = songService.isArtistHasSongs(artist.getKey());
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
            songCounter += songs.size();
            songService.save(songs);
        }
        if(artists.size() == 0)
            System.out.println("No more songs to save");
        return "Artists - " + artists.size() + "; Songs - " + songCounter;
    }


    public void deleteTable(String t){
        Table table = db.getTable(t);
        try {
            System.out.println("Attempting to delete table; please wait...");
            table.delete();
            table.waitForDelete();
            System.out.print("Success.");
        }
        catch (Exception e) {
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }
    }

    public void populateArtists(String[][] artists){
        ArtistService a = new ArtistService();
        for(int x = 0; x < artists.length; x++){
            a.create(artists[x][0],artists[x][1]);
        }
    }









}
