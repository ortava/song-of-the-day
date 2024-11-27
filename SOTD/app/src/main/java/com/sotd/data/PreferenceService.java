package com.sotd.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import com.sotd.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreferenceService {
    private final int DEFAULT_MIN_DURATION;
    private final int DEFAULT_MAX_DURATION;
    private final int DEFAULT_TEMPO;
    private final int DEFAULT_MOST;

    private final Set<String> defaultGenreSeeds;
    private final String[] toggleableAttributes = {
            "acousticness", "danceability", "energy", "instrumentalness", "liveness",
            "popularity", "speechiness", "tempo", "valence"
    };

    private SharedPreferences sharedPreferences;

    public PreferenceService(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        defaultGenreSeeds = new HashSet<>();
        defaultGenreSeeds.add("classical");

        Resources r = context.getResources();
        DEFAULT_MIN_DURATION = r.getInteger(R.integer.pref_default_min_duration);
        DEFAULT_MAX_DURATION = r.getInteger(R.integer.pref_default_max_duration);
        DEFAULT_TEMPO = r.getInteger(R.integer.pref_default_tempo);
        DEFAULT_MOST = r.getInteger(R.integer.pref_default_most);
    }

    public Set<String> getSelectedGenres() {
        Set<String> selectedGenres = sharedPreferences.getStringSet("selected_genres", null);
        if(selectedGenres != null && selectedGenres.size() > 0){
            return selectedGenres;
        } else {
            return defaultGenreSeeds;
        }
    }

    public Set<String> getEnabledAttributes() {
        Set<String> enabledAttributes = new HashSet<>();

        for(String key : toggleableAttributes) {
            if(sharedPreferences.getBoolean(key + "_enabled", true))
                enabledAttributes.add(key);
        }

        return enabledAttributes;
    }

    public String getAttributeAsString(String key) {
        if(!Arrays.asList(toggleableAttributes).contains(key))
            return "";

        switch (key) {
            case "min_duration":
                return String.valueOf(sharedPreferences.getInt(key, DEFAULT_MIN_DURATION * 1000) * 1000);
            case "max_duration":
                return String.valueOf(sharedPreferences.getInt(key, DEFAULT_MAX_DURATION * 1000) * 1000);
            case "popularity":
                return String.valueOf(sharedPreferences.getInt(key, DEFAULT_MOST));
            case "tempo":
                return String.valueOf(sharedPreferences.getInt(key, DEFAULT_TEMPO));
            default:
                return String.valueOf(sharedPreferences.getInt(key, DEFAULT_MOST) * 1.0 / 100);
        }
    }

    public boolean isUsingTrackAttributes() {
        return sharedPreferences.getBoolean("toggle_track_attributes", false);
    }

    public int getMinDuration() {
        return sharedPreferences.getInt("min_duration", DEFAULT_MIN_DURATION * 1000) * 1000;
    }

    public int getMaxDuration() {
        return sharedPreferences.getInt("max_duration", DEFAULT_MAX_DURATION * 1000) * 1000;
    }

    public double getAcousticness() {
        return sharedPreferences.getInt("acousticness", DEFAULT_MOST) * 1.0 / 100;
    }

    public double getDanceability() {
        return sharedPreferences.getInt("danceability", DEFAULT_MOST) * 1.0 / 100;
    }

    public double getEnergy() {
        return sharedPreferences.getInt("energy", DEFAULT_MOST) * 1.0 / 100;
    }

    public double getInstrumentalness() {
        return sharedPreferences.getInt("instrumentalness", DEFAULT_MOST) * 1.0 / 100;
    }

    public double getLiveness() {
        return sharedPreferences.getInt("liveness", DEFAULT_MOST) * 1.0 / 100;
    }

    public int getPopularity() {
        return sharedPreferences.getInt("popularity", DEFAULT_MOST);
    }

    public double getSpeechiness() {
        return sharedPreferences.getInt("speechiness", DEFAULT_MOST) * 1.0 / 100;
    }

    public int getTempo() {
        return sharedPreferences.getInt("tempo", DEFAULT_TEMPO);
    }

    public double getValence() {
        return sharedPreferences.getInt("valence", DEFAULT_MOST) * 1.0 / 100;
    }
}
