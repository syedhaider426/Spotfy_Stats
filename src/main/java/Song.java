
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Song")
public class Song {
    private String artist;
    private String song;
    private String releaseDate;
    private String uri;
    private String link;
    private float acousticness;
    private float danceability;
    private float energy;
    private float instrumentalness;
    private float liveness;
    private float loudness;
    private float speechiness;
    private float valence;
    private float tempo;

    public Song() {
    }

    @DynamoDBHashKey(attributeName = "artist")
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @DynamoDBHashKey(attributeName = "song")
    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    @DynamoDBHashKey(attributeName = "song")
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @DynamoDBHashKey(attributeName = "uri")
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @DynamoDBHashKey(attributeName = "link")
    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @DynamoDBHashKey(attributeName = "acousticness")
    public float getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(float acousticness) {
        this.acousticness = acousticness;
    }

    @DynamoDBHashKey(attributeName = "danceability")
    public float getDanceability() {
        return danceability;
    }

    public void setDanceability(float danceability) {
        this.danceability = danceability;
    }

    @DynamoDBHashKey(attributeName = "energy")
    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    @DynamoDBHashKey(attributeName = "instrumentalness")
    public float getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(float instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    @DynamoDBHashKey(attributeName = "liveness")
    public float getLiveness() {
        return liveness;
    }

    public void setLiveness(float liveness) {
        this.liveness = liveness;
    }

    @DynamoDBHashKey(attributeName = "loudness")
    public float getLoudness() {
        return loudness;
    }

    public void setLoudness(float loudness) {
        this.loudness = loudness;
    }

    @DynamoDBHashKey(attributeName = "speechiness")
    public float getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(float speechiness) {
        this.speechiness = speechiness;
    }

    @DynamoDBHashKey(attributeName = "valence")
    public float getValence() {
        return valence;
    }

    public void setValence(float valence) {
        this.valence = valence;
    }

    @DynamoDBHashKey(attributeName = "tempo")
    public float getTempo() {
        return tempo;
    }

    public void setTempo(float tempo) {
        this.tempo = tempo;
    }

    @DynamoDBIgnore
    public String toString() {
        return "Song{artist='" + artist
                + "', song='" + song
                + "', releaseDate='" + releaseDate
                + "', uri='" + uri
                + "', link='" + link
                + "', acousticness=" + acousticness
                + ", danceability=" + danceability
                + ", energy=" + energy
                + ", instrumentalness=" + instrumentalness
                + ", liveness=" + liveness
                + ", loudness=" + loudness
                + ", speechiness=" + speechiness
                + ", valence=" + valence
                + ", tempo=" + tempo + "}";
    }
}
