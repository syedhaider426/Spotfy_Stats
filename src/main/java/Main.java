
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Track;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Spotify spotify = new Spotify();
        Database db = new Database();
        String spotifyId = "7dtX3ykcuyVmts2HQnWgSP";
        String artistName = "Kompany";
        List<String> albumReleases = spotify.getReleases(spotifyId);
        List<String> tracks = spotify.getAlbumTracks(artistName, albumReleases);
        System.out.println("How many tracks?  ---- " + tracks.size());
        List<Track> songInfoList = spotify.getSongInfo(tracks);
        List<AudioFeatures> audioFeatureList = spotify.getSeveralTrackFeatures(tracks);
        List<Song> songs = new ArrayList();
        int x = 0;

        for(AudioFeatures audioFeature: audioFeatureList) {
            String name = songInfoList.get(x).getName();
            String releaseDate = songInfoList.get(x).getAlbum().getReleaseDate();
            String externalUrl = songInfoList.get(x).getAlbum().getExternalUrls().get("spotify");
            Song newSong = db.createSong(artistName, name, releaseDate, externalUrl, audioFeature);
            songs.add(newSong);
            x++;
        }

        System.out.println("How many songs - " + songs.size());
    }
}
