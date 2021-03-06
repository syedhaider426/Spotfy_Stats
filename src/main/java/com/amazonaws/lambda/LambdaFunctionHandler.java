package com.amazonaws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import org.json.JSONObject;
import stats.services.DynamoDBService;
import stats.services.SongService;

public class LambdaFunctionHandler implements RequestHandler<DynamodbEvent, Integer> {

    /**
     * Lambda function is triggered upon DynamoDB inserting/updating/deleting a record into the Artist table.
     * The function will get the artist and populate the Songs table with the songs the artist
     * has created. These songs are returned from making a request to Spotify's API.
     * @param event DML (insert,update,delete)
     * @param context (logger)
     * @return number of records processed
     */
    @Override
    public Integer handleRequest(DynamodbEvent event, Context context) {
        context.getLogger().log("Received event: " + event);
        JSONObject jo;
        DynamoDBService db = new DynamoDBService();
        SongService songService = new SongService();
        String spotifyId, artist, results = "";
        for (DynamodbStreamRecord record : event.getRecords()) {
            context.getLogger().log(record.getEventID());
            context.getLogger().log(record.getEventName());
            if(record.getEventName().equals("INSERT")) {
                // SuccessRecord will be a JSON that contains the new artist and their spotifyId
                String successRecord = record.getDynamodb().toString();
                jo = new JSONObject(successRecord);
                artist = jo.getString("artist");
                spotifyId = jo.getString("spotifyId");

                // If the artist does not have any songs in the Song table, execute
                if(!songService.isArtistHasSongs(artist)) {
                    results = db.populateSongs(artist, spotifyId);
                    context.getLogger().log(results);
                }
            }
        }
        return event.getRecords().size();
    }
}


