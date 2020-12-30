
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Spotify spotify = new Spotify();
        Database db = new Database();
        Map<String, String> artistsMap = db.getArtists();
        Map<String, String> artists = new HashMap<>();
        for (Map.Entry<String, String> artist : artistsMap.entrySet()) {
            boolean result = db.getSongs(artist.getKey());
            if (result)
                artists.put(artist.getKey(), artist.getValue());
        }
        System.out.println("Size of artists: " + artists.size());
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
            List<String> trackList = audioFeatureList
                    .stream()
                    .map(track -> track.getId())
                    .collect(Collectors.toList());
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
                Song newSong = db.createSong(artistName, name, releaseDate, externalUrl, audioFeature);
                songs.add(newSong);
                x++;
            }
                db.saveSong(songs);
        }
        if(artists.size() == 0)
            System.out.println("No more songs to save");

    }
}
