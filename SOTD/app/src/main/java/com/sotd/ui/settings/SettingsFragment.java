package com.sotd.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.recyclerview.widget.RecyclerView;

import com.sotd.R;
import com.sotd.spotify.SpotifyWebAPICommunicator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Map<String, String> keyTitleMap;
    private SpotifyWebAPICommunicator mSpotifyWebAPICommunicator;
    private SharedPreferences mSharedPreferences;

    private MultiSelectListPreference mMSListGenres;
    private SeekBarPreference mSeekBarMinDuration;
    private SeekBarPreference mSeekBarMaxDuration;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        mSharedPreferences = getPreferenceManager().getSharedPreferences();
        mSpotifyWebAPICommunicator = new SpotifyWebAPICommunicator(requireContext());

        // Fill map with toggleable preference keys and their respective title String resources.
        keyTitleMap = new HashMap<>();
        keyTitleMap.put("acousticness", getResources().getString(R.string.pref_title_acousticness));
        keyTitleMap.put("danceability", getResources().getString(R.string.pref_title_danceability));
        keyTitleMap.put("energy", getResources().getString(R.string.pref_title_energy));
        keyTitleMap.put("instrumentalness", getResources().getString(R.string.pref_title_instrumentalness));
        keyTitleMap.put("liveness", getResources().getString(R.string.pref_title_liveness));
        keyTitleMap.put("loudness", getResources().getString(R.string.pref_title_loudness));
        keyTitleMap.put("popularity", getResources().getString(R.string.pref_title_popularity));
        keyTitleMap.put("speechiness", getResources().getString(R.string.pref_title_speechiness));
        keyTitleMap.put("tempo", getResources().getString(R.string.pref_title_tempo));
        keyTitleMap.put("valence", getResources().getString(R.string.pref_title_valence));

        // Ensure toggleable preferences are titled appropriately.
        for(String key : keyTitleMap.keySet()) {
            Preference preference = findPreference(key);
            if(preference == null) break;
            if(mSharedPreferences.getBoolean(key + "_enabled", true)){
                preference.setTitle(keyTitleMap.get(key));
            } else {
                preference.setTitle(keyTitleMap.get(key) + " (disabled)");
            }
        }

        // Fill the multi-select list with Spotify's available genres.
        mMSListGenres = findPreference("selected_genres");
        mSpotifyWebAPICommunicator.getAvailableGenreSeeds(() -> {
            mMSListGenres.setEntries(mSpotifyWebAPICommunicator.getGenreSeeds());
            mMSListGenres.setEntryValues(mSpotifyWebAPICommunicator.getGenreSeeds());
        });

        // Stop the user from selecting more than 5 genre seeds.
        mMSListGenres.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                Set values = (Set)newValue;
                if(values.size() > 5) {
                    Toast.makeText(getContext(), "Changes not saved. Please select 5 genres or less.", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });

        // Stop min/max duration values from overlapping.
        mSeekBarMinDuration = findPreference("min_duration");
        mSeekBarMaxDuration = findPreference("max_duration");
        mSeekBarMinDuration.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if((int) newValue > mSeekBarMaxDuration.getValue() - (mSeekBarMaxDuration.getMin() - mSeekBarMinDuration.getMin())) {
                    mSeekBarMaxDuration.setValue((int) newValue + (mSeekBarMaxDuration.getMin() - mSeekBarMinDuration.getMin()));
                }
                return true;
            }
        });
        mSeekBarMaxDuration.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if((int) newValue < mSeekBarMinDuration.getValue() + (mSeekBarMaxDuration.getMin() - mSeekBarMinDuration.getMin())) {
                    mSeekBarMinDuration.setValue((int) newValue - (mSeekBarMaxDuration.getMin() - mSeekBarMinDuration.getMin()));
                }
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add padding to the bottom of the PreferenceScreen so the nav-bar doesn't obscure the last preference item.
        final RecyclerView rv = getListView();
        rv.setPadding(0, 0, 0, 200);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        String key = preference.getKey();
        if(keyTitleMap.containsKey(key)){
            if(mSharedPreferences.getBoolean(key + "_enabled", true)) {
                mSharedPreferences.edit().putBoolean(key + "_enabled", false).apply();
                preference.setTitle(keyTitleMap.get(key) + " (disabled)");
            } else {
                mSharedPreferences.edit().putBoolean(key + "_enabled", true).apply();
                preference.setTitle(keyTitleMap.get(key));
            }
        }
        return super.onPreferenceTreeClick(preference);
    }
}