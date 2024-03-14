package com.example.sotdprototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TrackService {
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Track songOfTheDay;

    public TrackService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        songOfTheDay = new Track();
    }

    public Track getSongOfTheDay() { return songOfTheDay; }

    public Track getTrackById(String trackId, final VolleyCallBack callBack) {
        Track track = new Track();
        String endpoint = "https://api.spotify.com/v1/tracks/" + trackId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    track.setAll(buildTrackFromJSONTrackObject(response));
                    callBack.onSuccess();
        }, error -> {
                    // TODO: Handle error.
                    Log.e("API ERROR", "Could not get track by ID.");
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
        return track;
    }

    public Track getRecommendation(String[] seeds, final VolleyCallBack callBack) {
        StringBuilder endpoint = new StringBuilder();

        endpoint.append("https://api.spotify.com/v1/recommendations");
        endpoint.append("?limit=1");
        endpoint.append("&seed_genres=");
        for(String seed : seeds){
            endpoint.append(seed);
            endpoint.append("%2C");
        }
        endpoint.delete(endpoint.length() - 3, endpoint.length()); // remove the extra "%2"

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint.toString(), null, response -> {
                    try {
                        JSONArray tracksArray = response.getJSONArray("tracks");
                        JSONObject trackObject = tracksArray.getJSONObject(0);
                        songOfTheDay.setAll(buildTrackFromJSONTrackObject(trackObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error.
                    Log.e("API ERROR", "Could not get a recommendation.");
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
        return songOfTheDay;
    }

    private Track buildTrackFromJSONTrackObject(JSONObject trackObject) {
        Track track = new Track();

        try {
            JSONObject albumObject = trackObject.getJSONObject("album");
            JSONArray images = albumObject.getJSONArray("images");
            JSONObject image = images.getJSONObject(1); // index 1 -> Image Dimensions: 300x300
            JSONArray artistsArray = trackObject.getJSONArray("artists");
            JSONObject artistObject = artistsArray.getJSONObject(0);

            track.setSpotifyId(trackObject.getString("id"));
            track.setUri(trackObject.getString("uri"));
            track.setTitle(trackObject.getString("name"));
            track.setAlbum(albumObject.getString("name"));
            track.setArtist(artistObject.getString("name"));
            track.setCoverURL(image.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return track;
    }
}
