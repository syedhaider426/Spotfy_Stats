package com.amazonaws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import org.json.JSONObject;
import stats.models.Artist;
import stats.services.ArtistService;
import stats.services.DynamoDBService;
import stats.services.SpotifyService;

public class LambdaFunctionHandler implements RequestHandler<DynamodbEvent, Integer> {

    @Override
    public Integer handleRequest(DynamodbEvent event, Context context) {
        context.getLogger().log("Received event: " + event);
        JSONObject jo;
        SpotifyService spotify = new SpotifyService();
        DynamoDBService db = new DynamoDBService();
        ArtistService artistService = new ArtistService();
        String spotifyId;
        String results = "";
        for (DynamodbStreamRecord record : event.getRecords()) {
            context.getLogger().log(record.getEventID());
            context.getLogger().log(record.getEventName());
            if(record.getEventName().equals("INSERT")) {
                String successRecord = record.getDynamodb().toString();
                jo = new JSONObject(successRecord);
                String artist = jo.getString("artist");
                context.getLogger().log("Artist found -" + artist);
                Artist a = db.getMapper().load(Artist.class,artist);
                if(a.getSpotifyId().length() > 0) {
                    context.getLogger().log("Songs have already been queried for and exist in database.");
                    continue;
                }
                spotifyId = spotify.searchForArtist(artist,context);
                artistService.update(artist,spotifyId);
                try {
                    results = db.populateSongs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(results.length() == 0){
                    context.getLogger().log("No songs found for Artist - " + artist);
                }
                else {
                    context.getLogger().log(results);
                }
            }
        }
        return event.getRecords().size();
    }
}


