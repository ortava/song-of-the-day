package com.example.sotdprototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TrackService {
    private Track track;

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public TrackService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        track = new Track();
    }

    public Track getTrack(){
        return this.track;
    }

    public Track getTrackById(String trackId, final VolleyCallBack callBack) {
        //String endpoint = "https://api.spotify.com/v1/tracks/{id}";
        String endpoint = "https://api.spotify.com/v1/tracks/" + trackId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    try {
                        JSONObject albumObject = response.getJSONObject("album");
                        JSONArray artistsArray = albumObject.getJSONArray("artists");
                        JSONObject artistObject = artistsArray.getJSONObject(0);
                        String trackUri = response.getString("uri");
                        String trackTitle = response.getString("name");

                        track.setId(trackId);
                        track.setUri(trackUri);
                        track.setTitle(trackTitle);
                        track.setAlbum(albumObject.getString("name"));
                        track.setArtist(artistObject.getString("name"));
                        JSONArray images = albumObject.getJSONArray("images");
                        JSONObject image = images.getJSONObject(1); // index 1 -> Image Dimensions: 300x300
                        track.setCoverURL(image.getString("url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
}
