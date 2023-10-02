package com.example.sotdprototype;

import android.content.Context;
import android.content.SharedPreferences;

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
                    //JSONObject object = response.optJSONObject("name");
                    try {
                        String trackTitle = response.getString("name");
                        String trackUri = response.getString("uri");
                        track.setTitle(trackTitle);
                        track.setUri(trackUri);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    /*
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray(name);

                    for(int i = 0; i < jsonArray.length(); i++){
                        try {
                            JSONObject object = jsonArray.getJSONObject(i);
                            object = object.optJSONObject("track");
                            track = gson.fromJson(object.toString(), Track.class);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                     */

                    callBack.onSuccess();
        }, error -> {
                    // TODO: Handle error.
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
