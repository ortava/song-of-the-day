package com.example.sotdprototype.ui.settings;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sotdprototype.R;
import com.example.sotdprototype.databinding.FragmentSettingsBinding;
import com.example.sotdprototype.data.db.TrackService;

public class SettingsFragment extends PreferenceFragmentCompat {
    private FragmentSettingsBinding binding;
    private TrackService mTrackService;

    private MultiSelectListPreference mMSListGenres;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        mTrackService = new TrackService(requireContext());

        mMSListGenres = findPreference("selected_genres");
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