package com.sotd.spotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sotd.data.PreferenceService;
import com.sotd.data.db.Track;
import com.sotd.data.db.TrackService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SpotifyWebAPICommunicator {
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Track songOfTheDay;
    private ArrayList<String> genreSeeds;
    private PreferenceService preferenceService;

    private TrackService trackService;

    public SpotifyWebAPICommunicator(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        preferenceService = new PreferenceService(context);
        songOfTheDay = new Track();
        genreSeeds = new ArrayList<>();

        trackService = new TrackService(context);
    }

    public Track getSongOfTheDay() {
        return songOfTheDay;
    }

    public String[] getGenreSeeds() {
        return genreSeeds.toArray(new String[0]);
    }

    public Track getTrackById(String trackId, final VolleyCallBack callBack) {
        Track track = new Track();
        String endpoint = "https://api.spotify.com/v1/tracks/" + trackId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    track.setAll(buildTrackFromJSONTrackObject(response));
                    callBack.onSuccess();
                }, error -> {
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

    public Track getRecommendation(final VolleyCallBack callBack) {
        StringBuilder endpoint = new StringBuilder();

        endpoint.append("https://api.spotify.com/v1/recommendations");
        endpoint.append("?limit=100");

        // Use selected genre seeds in the request.
        endpoint.append("&seed_genres=");
        for (String seed : preferenceService.getSelectedGenres()) {
            endpoint.append(seed).append("%2C");
        }
        endpoint.delete(endpoint.length() - 3, endpoint.length()); // remove the extra "%2C"

        // Use selected track attributes in the request.
        if (preferenceService.isUsingTrackAttributes()) {
            endpoint.append("&min_duration_ms=").append(preferenceService.getMinDuration());
            endpoint.append("&max_duration_ms=").append(preferenceService.getMaxDuration());
            for (String key : preferenceService.getEnabledAttributes()) {
                endpoint.append("&target_").append(key).append("=").append(preferenceService.getAttributeAsString(key));
            }
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint.toString(), null, response -> {
                    try {
                        JSONArray tracksArray = response.getJSONArray("tracks");

                        // Randomly select a track from the tracks array.
                        Random rand = new Random();
                        int index = rand.nextInt(tracksArray.length());
                        Set<Integer> checkedIndices = new HashSet<>();
                        JSONObject trackObject = tracksArray.getJSONObject(index);

                        // Make sure that the track hasn't been recommended recently (it would already be in the database).
                        while (trackService.isInDatabase(trackObject.getString("id"))) {
                            checkedIndices.add(index);              // The track at this index is already in the database.
                            while (checkedIndices.contains(index)) { // Keep randomly selecting indices until we find one that hasn't been checked before.
                                index = rand.nextInt(tracksArray.length());
                            }
                            trackObject = tracksArray.getJSONObject(index);
                        }
                        // Set this track as Song Of The Day.
                        songOfTheDay.setAll(buildTrackFromJSONTrackObject(trackObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onSuccess();
                }, error -> {
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
                        for (int i = 0; i < genreArray.length(); i++) {
                            genreSeeds.add(genreArray.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onSuccess();
                }, error -> {
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
            track.setDuration(trackObject.getInt("duration_ms"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return track;
    }

    //TODO: Put token methods into a separate class?
    /// AUTHORIZATION TOKEN METHODS
    public void acquireAuthTokens(String authCode, String clientId, String redirectURI, final VolleyCallBack callBack) {
        String endpoint = "https://accounts.spotify.com/api/token";

        // Getting Status 400 (Bad Request) "Invalid code_verifier" when using JsonObjectRequest.
        // Works with StringRequest though...
        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, endpoint, response -> {
                    try {
                        //TODO: Save both refresh and access tokens with their appropriate labels.
                        // (currently only saves access token to accommodate pre-PKCE implementation)
                        JSONObject jsonResponse = new JSONObject(response);
                        String refreshToken = jsonResponse.getString("refresh_token");
                        String accessToken = jsonResponse.getString("access_token");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", accessToken);
                        editor.apply();
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                    callBack.onSuccess();
                }, error -> {
                    Log.e("API ERROR", "Could not get authorization tokens.");
                    Log.e("API ERROR", error.networkResponse.toString());
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("content-type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "authorization_code");
                params.put("code", authCode);
                params.put("redirect_uri", redirectURI);
                params.put("client_id", clientId);
                params.put("code_verifier", sharedPreferences.getString("code_verifier", ""));

                return params;
            }
        };

        queue.add(stringRequest);
    }

    //TODO: Implement token-swap method (use Refresh Token to retrieve a new Access Token).
}
