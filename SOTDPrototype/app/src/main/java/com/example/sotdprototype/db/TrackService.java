package com.example.sotdprototype.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sotdprototype.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackService {
    public static final int MAX_DATASET_COUNT = 30;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Track songOfTheDay;
    private ArrayList<String> genreSeeds;

    private TrackDAO trackDAO;

    public TrackService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        songOfTheDay = new Track();
        genreSeeds = new ArrayList<>();

        AppDatabase db = AppDatabase.getDbInstance(context);
        trackDAO = db.trackDAO();
    }

    public Track getSongOfTheDay() { return songOfTheDay; }
    public String[] getGenreSeeds() { return genreSeeds.toArray(new String[0]); }

    /// API METHODS
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

    // TODO: Find a better name for either getAvailableGenreSeeds() or getGenreSeeds(). They are too similar.
    public String[] getAvailableGenreSeeds(VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/recommendations/available-genre-seeds";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    try {
                        JSONArray genreArray = response.getJSONArray("genres");
                        for(int i = 0; i < genreArray.length(); i++) {
                            genreSeeds.add(genreArray.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error.
                    Log.e("API ERROR", "Could not get genre seeds.");
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
        return genreSeeds.toArray(new String[0]);
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

    /// DATABASE METHODS
    public void addTrackToDataBase(Track track) {
        if(trackDAO.getCount() >= MAX_DATASET_COUNT) {
            trackDAO.deleteTopRow();
        }
        trackDAO.insert(track);
    }

    public List<Track> getAllTracksFromDataBase() {
        return trackDAO.getAll();
    }
}
