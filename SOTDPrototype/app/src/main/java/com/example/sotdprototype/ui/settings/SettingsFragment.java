package com.example.sotdprototype.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sotdprototype.R;
import com.example.sotdprototype.databinding.FragmentSettingsBinding;
import com.example.sotdprototype.data.db.TrackService;

import java.util.Set;

public class SettingsFragment extends PreferenceFragmentCompat {
    private FragmentSettingsBinding binding;
    private TrackService mTrackService;

    private MultiSelectListPreference mMSListGenres;
    private SeekBarPreference mSeekBarMinDuration;
    private SeekBarPreference mSeekBarMaxDuration;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        mTrackService = new TrackService(requireContext());

        mMSListGenres = findPreference("selected_genres");
        mTrackService.getAvailableGenreSeeds(() -> {
            mMSListGenres.setEntries(mTrackService.getGenreSeeds());
            mMSListGenres.setEntryValues(mTrackService.getGenreSeeds());
        });

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        binding = null;
    }
}