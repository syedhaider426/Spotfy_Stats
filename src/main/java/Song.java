
import com.amazonaws.services.dynamodbv2.datamodeling.*;

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
    private boolean hidden;

    public Song() {
    }

    @DynamoDBHashKey(attributeName = "artist")
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @DynamoDBAttribute(attributeName = "song")
    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    @DynamoDBRangeKey(attributeName = "releaseDate")
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @DynamoDBAttribute(attributeName = "uri")
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @DynamoDBAttribute(attributeName = "link")
    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @DynamoDBAttribute(attributeName = "acousticness")
    public float getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(float acousticness) {
        this.acousticness = acousticness;
    }

    @DynamoDBAttribute(attributeName = "danceability")
    public float getDanceability() {
        return danceability;
    }

    public void setDanceability(float danceability) {
        this.danceability = danceability;
    }

    @DynamoDBAttribute(attributeName = "energy")
    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    @DynamoDBAttribute(attributeName = "instrumentalness")
    public float getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(float instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    @DynamoDBAttribute(attributeName = "liveness")
    public float getLiveness() {
        return liveness;
    }

    public void setLiveness(float liveness) {
        this.liveness = liveness;
    }

    @DynamoDBAttribute(attributeName = "loudness")
    public float getLoudness() {
        return loudness;
    }

    public void setLoudness(float loudness) {
        this.loudness = loudness;
    }

    @DynamoDBAttribute(attributeName = "speechiness")
    public float getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(float speechiness) {
        this.speechiness = speechiness;
    }

    @DynamoDBAttribute(attributeName = "valence")
    public float getValence() {
        return valence;
    }

    public void setValence(float valence) {
        this.valence = valence;
    }

    @DynamoDBAttribute(attributeName = "tempo")
    public float getTempo() {
        return tempo;
    }

    public void setTempo(float tempo) {
        this.tempo = tempo;
    }

    @DynamoDBAttribute(attributeName = "hidden")
    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @DynamoDBIgnore
    @Override
    public String toString() {
        return "Song{artist='" + artist
                + "', song='" + song
                + "', releaseDate='" + releaseDate + "}";
    }
}
