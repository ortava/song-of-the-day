package com.example.sotdprototype.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sotdprototype.data.PreferenceService;
import com.example.sotdprototype.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TrackService {
    public static final int MAX_DATASET_COUNT = 30;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Track songOfTheDay;
    private ArrayList<String> genreSeeds;
    private PreferenceService preferenceService;

    private TrackDAO trackDAO;

    public TrackService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        preferenceService = new PreferenceService(context);
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

    public Track getRecommendation(final VolleyCallBack callBack) {
        StringBuilder endpoint = new StringBuilder();

        endpoint.append("https://api.spotify.com/v1/recommendations");
        endpoint.append("?limit=100");

        // Use selected genre seeds in the request.
        endpoint.append("&seed_genres=");
        for(String seed : preferenceService.getSelectedGenres()){
            endpoint.append(seed).append("%2C");
        }
        endpoint.delete(endpoint.length() - 3, endpoint.length()); // remove the extra "%2C"

        // Use selected track attributes in the request.
        if(preferenceService.isUsingAudioFeatures()){
            endpoint.append("&min_duration_ms=").append(preferenceService.getMinDuration());
            endpoint.append("&max_duration_ms=").append(preferenceService.getMaxDuration());
            if(preferenceService.isUsingAcousticness())
                endpoint.append("&target_acousticness=").append(preferenceService.getAcousticness());
            if(preferenceService.isUsingDanceability())
                endpoint.append("&target_danceability=").append(preferenceService.getDanceability());
            if(preferenceService.isUsingEnergy())
                endpoint.append("&target_energy=").append(preferenceService.getEnergy());
            if(preferenceService.isUsingInstrumentalness())
                endpoint.append("&target_instrumentalness=").append(preferenceService.getInstrumentalness());
            if(preferenceService.isUsingLiveness())
                endpoint.append("&target_liveness=").append(preferenceService.getLiveness());
            if(preferenceService.isUsingLoudness())
                endpoint.append("&target_loudness=").append(preferenceService.getLoudness());
            if(preferenceService.isUsingPopularity())
                endpoint.append("&target_popularity=").append(preferenceService.getPopularity());
            if(preferenceService.isUsingSpeechiness())
                endpoint.append("&target_speechiness=").append(preferenceService.getSpeechiness());
            if(preferenceService.isUsingTempo())
                endpoint.append("&target_tempo=").append(preferenceService.getTempo());
            if(preferenceService.isUsingValence())
                endpoint.append("&target_valence=").append(preferenceService.getValence());
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
                        while (isInDatabase(trackObject.getString("id"))) {
                            checkedIndices.add(index);              // The track at this index is already in the database.
                            while(checkedIndices.contains(index)) { // Keep randomly selecting indices until we find one that hasn't been checked before.
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

    public boolean isInDatabase(String id) {
        return trackDAO.getTrackBySpotifyId(id) != null;
    }
}
