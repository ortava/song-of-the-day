package com.example.sotdprototype.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.sotdprototype.R;
import com.example.sotdprototype.databinding.FragmentSettingsBinding;
import com.example.sotdprototype.db.TrackService;

public class SettingsFragment extends PreferenceFragmentCompat {
    private FragmentSettingsBinding binding;
    private TrackService mTrackService;

    private MultiSelectListPreference mMSListGenres;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        mTrackService = new TrackService(requireContext());

        mMSListGenres = findPreference("multi_select_list_genres");
        mTrackService.getAvailableGenreSeeds(() -> {
            mMSListGenres.setEntries(mTrackService.getGenreSeeds());
            mMSListGenres.setEntryValues(mTrackService.getGenreSeeds());
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}