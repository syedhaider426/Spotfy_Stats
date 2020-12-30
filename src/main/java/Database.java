
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Database {
    final private DynamoDB db;
    final private DynamoDBMapper mapper;

    public Database() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();
        this.mapper = new DynamoDBMapper(client);
        this.db = new DynamoDB(client);
    }

    public void createSongTable(){
        System.out.println("Attempting to create table; please wait...");
        String tableName = "Song";

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("id").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("releaseDate").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("artist").withAttributeType("S"));

        // Key schema for table
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("id").withKeyType(KeyType.HASH)); // Partition key
        tableKeySchema.add(new KeySchemaElement().withAttributeName("releaseDate").withKeyType(KeyType.RANGE)); // Sort key

        // Initial provisioned throughput settings for the indexes
        ProvisionedThroughput ptIndex = new ProvisionedThroughput()
                .withReadCapacityUnits(1L)
                .withWriteCapacityUnits(1L);

        // ArtistIndex
        GlobalSecondaryIndex artistIndex = new GlobalSecondaryIndex()
                .withIndexName("ArtistIndex")
                .withProvisionedThroughput(ptIndex)
                .withKeySchema(new KeySchemaElement().withAttributeName("artist").withKeyType(KeyType.HASH)) // Partition
                .withProjection(new Projection().withProjectionType("KEYS_ONLY"));

        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
                .withProvisionedThroughput(ptIndex)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(tableKeySchema)
                .withGlobalSecondaryIndexes(artistIndex);

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

    public void deleteTable(){
        Table table = db.getTable("Song");
        try {
            System.out.println("Attempting to delete table; please wait...");
            table.delete();
            table.waitForDelete();
            System.out.println("Success.");

        }
        catch (Exception e) {
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
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

    public void populateArtistTable() throws IOException {
        InputStream file = new FileInputStream("file.json");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> myObjects = objectMapper.readValue(file, new TypeReference<>(){});
        List<Artist> artists = new ArrayList<>();
        for (Map<String,Object> map : myObjects) {
            Artist artist = new Artist();
            artist.setArtist((String) map.get("Artist"));
            artist.setSpotifyId((String) map.get("SpotifyID"));
            artists.add(artist);
        }
        mapper.batchSave(artists);
        System.out.println("Added artists to database");
    }

    public Map<String,String> getArtists(){
        Map<String,String> artists = new HashMap<>();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Artist> scanResult = mapper.scan(Artist.class,scanExpression);
        for(Artist artist: scanResult){
            artists.put(artist.getArtist(),artist.getSpotifyId());
        }
        return artists;
    }

    public boolean getSongs(String artist) {
        final Song gsiKeyObj = new Song();
        gsiKeyObj.setArtist(artist);
        final DynamoDBQueryExpression<Song> queryExpression = new DynamoDBQueryExpression<Song>()
            .withHashKeyValues(gsiKeyObj)
            .withIndexName("ArtistIndex")
            .withConsistentRead(false);
        final PaginatedQueryList<Song> results = mapper.query(Song.class, queryExpression);
        return results.size() == 0;
    }

    public Song createSong(String artist, String track, String releaseDate, String externalUrl, AudioFeatures audioFeatures) {
        Song song = new Song();
        song.setAcousticness(audioFeatures.getAcousticness());
        song.setArtist(artist);
        song.setDanceability(audioFeatures.getDanceability());
        song.setEnergy(audioFeatures.getEnergy());
        song.setInstrumentalness(audioFeatures.getInstrumentalness());
        song.setLink(externalUrl);
        song.setLiveness(audioFeatures.getLiveness());
        song.setLoudness(audioFeatures.getLoudness());
        song.setReleaseDate(releaseDate + " - " + artist);
        song.setSong(track);
        song.setSpeechiness(audioFeatures.getSpeechiness());
        song.setTempo(audioFeatures.getTempo());
        song.setId(audioFeatures.getId());
        song.setValence(audioFeatures.getValence());
        song.setHidden(false);
        return song;
    }

    public void saveSong(List<Song> songs) {
        System.out.println("saving songs...........");
        mapper.batchSave(songs);
        System.out.println("SONGS ARE SAVED");
    }

    public void saveSong(Song song){
        mapper.save(song);
    }






}
