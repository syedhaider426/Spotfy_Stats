
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetSeveralTracksRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;

public class Spotify {
    private SpotifyApi spotifyApi;

    public Spotify() {
        try {
            GetPropertyValues properties = new GetPropertyValues();
            Properties prop = properties.getPropValues();
            this.spotifyApi = new SpotifyApi.Builder()
                    .setClientId(prop.getProperty("spotifyClientId"))
                    .setClientSecret(prop.getProperty("spotifyClientSecret"))
                    .build();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public List<String> getReleases(String artistId) {
        System.out.println("Loading releases");
        List<String> albumsList = new ArrayList<>();
        boolean counter = false;
        int offset = 0;

        while(!counter) {
            this.spotifyApi = this.setToken();
            GetArtistsAlbumsRequest getArtistsAlbumsRequest = this.spotifyApi
                    .getArtistsAlbums(artistId)
                    .limit(50)
                    .market(CountryCode.US)
                    .offset(offset)
                    .build();
            try {
                Paging<AlbumSimplified> albums = getArtistsAlbumsRequest.execute();
                AlbumSimplified[] items = albums.getItems();
                if (items.length == 0) {
                    counter = true;
                } else {
                    for (AlbumSimplified item : items) {
                        albumsList.add(item.getId());
                    }
                    offset += 50;
                }
            } catch (SpotifyWebApiException | ParseException | IOException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Ending releases");
        return albumsList;
    }

    public List<String> getAlbumTracks(String artistName, List<String> albumReleases) {
        System.out.println("Loading tracks");
        String name = artistName.toLowerCase();
        Map<String,String> originalList = new HashMap<>();
        List<String> tracksList = new ArrayList<>();
        try {
            for(String albumRelease: albumReleases){
                GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(albumRelease).build();
                Paging<TrackSimplified> trackSimplifiedPaging = getAlbumsTracksRequest.execute();
                TrackSimplified[] items = trackSimplifiedPaging.getItems();
                for(TrackSimplified item: items) {
                    ArtistSimplified[] artistsSimplified = item.getArtists();
                    for(ArtistSimplified artist: artistsSimplified) {
                        if (artist.getName().toLowerCase().equals(name)) {
                            String song = item.getName().toLowerCase();
                            System.out.println(song);
                            if(!song.contains("remix") || song.contains(artistName)) {
                                originalList.put(item.getName(),item.getId());
                                break;
                            }
                        }
                    }
                }
            }
            // Duplicates removed
            for (Map.Entry<String, String> entry : originalList.entrySet()) {
                tracksList.add(entry.getValue());
            }
            System.out.println("Ending tracks");
            return tracksList;
        } catch (SpotifyWebApiException | IOException | ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<AudioFeatures> getSeveralTrackFeatures(List<String> tracks) {
        System.out.println("Loading track features");
        String[] tracksArray = tracks.toArray(new String[0]);
        List<AudioFeatures> trackList = new ArrayList<>();
        int x = 0;
        try {
            while(x < tracksArray.length) {
                int y = x + 90;
                if (y > tracksArray.length) {
                    y = tracksArray.length;
                }
                String[] tempList = Arrays.copyOfRange(tracksArray, x, y);
                GetAudioFeaturesForSeveralTracksRequest getAudioFeaturesForSeveralTracksRequest = this.spotifyApi
                        .getAudioFeaturesForSeveralTracks(tempList)
                        .build();
                AudioFeatures[] audioFeatures = getAudioFeaturesForSeveralTracksRequest.execute();
                trackList.addAll(Arrays.asList(audioFeatures));
                x += 90;
            }
            List<AudioFeatures> audioFeaturesList =  trackList.stream()
                    .filter(p -> p != null)
                    .collect(Collectors.toList());
            System.out.println("Ending track features");
            return audioFeaturesList;
        } catch (SpotifyWebApiException | IOException | ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Track> getSongInfo(List<String> tracks) {
        System.out.println("Loading track information");
        String[] tracksArray = tracks.toArray(new String[0]);
        List<Track> trackList = new ArrayList<>();
        int x = 0;
        try {
            while(x < tracksArray.length) {
                int y = x + 45;
                if (y > tracksArray.length) {
                    y = tracksArray.length;
                }
                String[] tempList = Arrays.copyOfRange(tracksArray, x, y);
                GetSeveralTracksRequest getSeveralTracksRequest = spotifyApi.getSeveralTracks(tempList).build();
                Track[] tList = getSeveralTracksRequest.execute();
                trackList.addAll(Arrays.asList(tList));
                x += 45;
            }
            System.out.println("Ending track information");
            return trackList;
        } catch (SpotifyWebApiException | IOException | ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public SpotifyApi setToken() {
        try {
            ClientCredentialsRequest clientCredentialsRequest = this.spotifyApi.clientCredentials().build();
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            this.spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException ex) {
            ex.printStackTrace();
        }

        return this.spotifyApi;
    }
}
