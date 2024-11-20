package com.sotd.spotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sotd.data.PreferenceService;
import com.sotd.data.db.Track;
import com.sotd.data.db.TrackService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SpotifyWebAPIService {
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Track songOfTheDay;
    private ArrayList<String> genreSeeds;

    private PreferenceService preferenceService;
    private TrackService trackService;

    public SpotifyWebAPIService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        songOfTheDay = new Track();
        genreSeeds = new ArrayList<>();

        preferenceService = new PreferenceService(context);
        trackService = new TrackService(context);
    }

    public Track getSongOfTheDay() {
        return songOfTheDay;
    }

    public String[] getGenreSeeds() {
        return genreSeeds.toArray(new String[0]);
    }

    /**
     *  Makes a request to the Spotify Web API for a customized track recommendation.
     *  The recommendation is selected randomly from a list of the top tracks matching
     *  the user's recommendation preferences.
     *  @param  callBack    Volley callback function.
     *  @return The selected recommendation as a Track object.
     *              (This track is also saved as this.songOfTheDay for access within the callback)
     */
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
                    Log.d("SpotifyWebAPIService", "Got recommendation!");
                    callBack.onSuccess();
                }, error -> {
                    Log.e("SpotifyWebAPIService", "Could not get a recommendation.");
                    if(error.networkResponse.data != null) {
                        Log.e("SpotifyWebAPIService", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                    }
                    callBack.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("access_token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
        return songOfTheDay;
    }

    // TODO: Find a better name for either getAvailableGenreSeeds() or getGenreSeeds(). They are too similar.
    /**
     *  Makes a request to the Spotify Web API for a list of available genre seeds.
     *  These are the genre seeds that Spotify recognizes (for use in other API requests).
     *  @param  callBack    Volley callback function.
     *  @return A String[] of available genre seeds.
     *              (These genre seeds are also saved as this.genreSeeds for access within the callback)
     */
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
                    Log.d("SpotifyWebAPIService", "Got genre seeds!");
                    callBack.onSuccess();
                }, error -> {
                    Log.e("SpotifyWebAPIService", "Could not get genre seeds.");
                    if(error.networkResponse.data != null) {
                        Log.e("SpotifyWebAPIService", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                    }
                    callBack.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("access_token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
}
