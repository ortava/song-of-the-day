package com.example.sotdprototype.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import com.example.sotdprototype.R;

import java.util.HashSet;
import java.util.Set;

public class PreferenceService {
    private final int DEFAULT_MIN_DURATION;
    private final int DEFAULT_MAX_DURATION;
    private final int DEFAULT_TEMPO;
    private final int DEFAULT_MOST;

    private final Set<String> defaultGenreSeeds;

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

    public double getLoudness() {
        return sharedPreferences.getInt("loudness", DEFAULT_MOST) * 1.0 / 100;
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

    public int getMinDuration() {
        return sharedPreferences.getInt("min_duration", DEFAULT_MIN_DURATION * 1000) * 1000;
    }

    public int getMaxDuration() {
        return sharedPreferences.getInt("max_duration", DEFAULT_MAX_DURATION * 1000) * 1000;
    }

    public boolean isUsingAudioFeatures() {
        return sharedPreferences.getBoolean("toggle_audio_features", false);
    }

    public boolean isUsingAcousticness() {
        return sharedPreferences.getBoolean("acousticness_enabled", true);
    }
    public boolean isUsingDanceability() {
        return sharedPreferences.getBoolean("danceability_enabled", true);
    }
    public boolean isUsingEnergy() {
        return sharedPreferences.getBoolean("energy_enabled", true);
    }
    public boolean isUsingInstrumentalness() {
        return sharedPreferences.getBoolean("instrumentalness_enabled", true);
    }
    public boolean isUsingLiveness() {
        return sharedPreferences.getBoolean("liveness_enabled", true);
    }
    public boolean isUsingLoudness() {
        return sharedPreferences.getBoolean("loudness_enabled", true);
    }
    public boolean isUsingPopularity() {
        return sharedPreferences.getBoolean("popularity_enabled", true);
    }
    public boolean isUsingSpeechiness() {
        return sharedPreferences.getBoolean("speechiness_enabled", true);
    }
    public boolean isUsingTempo() {
        return sharedPreferences.getBoolean("tempo_enabled", true);
    }
    public boolean isUsingValence() {
        return sharedPreferences.getBoolean("valence_enabled", true);
    }
}
