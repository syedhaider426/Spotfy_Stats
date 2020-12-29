
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Database {
    private DynamoDB db;
    private DynamoDBMapper mapper;

    public Database() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();
        this.mapper = new DynamoDBMapper(client);
        this.db = new DynamoDB(client);
    }

    public void createSongTable() {
        try {
            System.out.println("Attempting to create table; please wait...");
            String tableName = "Song";
            Table table = this.db.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("artist", KeyType.HASH),
                            new KeySchemaElement("releaseDate", KeyType.RANGE)),
                    Arrays.asList(new AttributeDefinition("artist", ScalarAttributeType.S),
                            new AttributeDefinition("releaseDate", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success. Table status: " + table.getDescription().getTableStatus());
        } catch (ResourceInUseException ex) {
            System.out.println("Table already exists");
        } catch (Exception ex) {
            System.err.println("Unable to create table: ");
            ex.printStackTrace();
        }

    }

    public void createArtistTable() {
        try {
            System.out.println("Attempting to create table; please wait...");
            String tableName = "Artist";
            Table table = db.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("artist", KeyType.HASH)),
                    Arrays.asList(new AttributeDefinition("artist", ScalarAttributeType.S)),
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
        for (Map map : myObjects) {
            Artist artist = new Artist();
            artist.setArtist((String) map.get("Artist"));
            artist.setSpotifyId((String) map.get("SpotifyID"));
            artists.add(artist);
        }
        mapper.batchSave(artists);
        System.out.println("Added artists to database");
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
        song.setReleaseDate(releaseDate);
        song.setSong(track);
        song.setSpeechiness(audioFeatures.getSpeechiness());
        song.setTempo(audioFeatures.getTempo());
        song.setUri(audioFeatures.getId());
        song.setValence(audioFeatures.getValence());
        System.out.println(song);
        System.out.println("-------------");
        return song;
    }

    public void saveSong(ArrayList<Song> songs) {
        mapper.batchSave(songs);
    }
}
