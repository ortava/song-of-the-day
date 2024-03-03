package com.example.sotdprototype.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.example.sotdprototype.AuthorizeActivity;
import com.example.sotdprototype.MainActivity;
import com.example.sotdprototype.R;
import com.example.sotdprototype.Track;
import com.example.sotdprototype.TrackService;
import com.example.sotdprototype.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel mHomeViewModel;
    private TrackService mTrackService;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHomeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        mTrackService = new TrackService(requireContext());

        setSongOfTheDay();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewHome = binding.textHome;
        mHomeViewModel.getHomeText().observe(getViewLifecycleOwner(), textViewHome::setText);

        final TextView textViewTitle = binding.textTitle;
        mHomeViewModel.getTitleText().observe(getViewLifecycleOwner(), textViewTitle::setText);

        final TextView textViewArtist = binding.textArtist;
        mHomeViewModel.getArtistText().observe(getViewLifecycleOwner(), textViewArtist::setText);

        final ImageButton buttonPlay = binding.buttonPlay;
        buttonPlay.setOnClickListener(v -> mHomeViewModel.getSpotifyTrackURI().observe(getViewLifecycleOwner(), ((MainActivity) getActivity())::remotePlay));

        final Button buttonOpenTrackInSpotify = binding.buttonOpenTrackInSpotify;
        buttonOpenTrackInSpotify.setOnClickListener(v -> mHomeViewModel.getSpotifyTrackURI().observe(getViewLifecycleOwner(), ((MainActivity) getActivity())::openTrackInSpotify));

        mHomeViewModel.getCoverURL().observe(getViewLifecycleOwner(), this::loadCoverImage);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadCoverImage(String url){
        if (!url.isEmpty()){
            Picasso.get()
                    .load(url)
                    .error(R.drawable.placeholder_cover)
                    .into(binding.imageCover);
        }
    }

    private void setSongOfTheDay() {
        // TODO: Allow for user-selected seeds as opposed to the current baked in version.
        String[] seedGenres = {"classical", "ambient", "j-rock"};
        mTrackService.getRecommendation(seedGenres, () -> {
            Log.d("API", "GOT RECOMMENDATION");
            mHomeViewModel.setTrack(mTrackService.getSongOfTheDay());
        });
    }
}