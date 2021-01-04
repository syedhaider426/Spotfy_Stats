package com.amazonaws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import org.json.JSONObject;
import stats.config.DynamoDBConfiguration;
import stats.services.ArtistService;
import stats.services.SpotifyService;

public class LambdaFunctionHandler implements RequestHandler<DynamodbEvent, Integer> {

    @Override
    public Integer handleRequest(DynamodbEvent event, Context context) {
        context.getLogger().log("Received event: " + event);

        for (DynamodbStreamRecord record : event.getRecords()) {
            context.getLogger().log(record.getEventID());
            context.getLogger().log(record.getEventName());
            if(record.getEventName().equals("INSERT")) {
                String successRecord = record.getDynamodb().toString();
                JSONObject jo = new JSONObject(successRecord);
                SpotifyService spotify = new SpotifyService();
                String artist = jo.getString("artist");
                String spotifyId = spotify.searchForArtist(artist,context);
                ArtistService artistService = new ArtistService();
                artistService.update(artist,spotifyId);
                DynamoDBConfiguration db = new DynamoDBConfiguration();
                String results = db.loadData();
                context.getLogger().log(results);
            }
        }
        return event.getRecords().size();
    }
}


