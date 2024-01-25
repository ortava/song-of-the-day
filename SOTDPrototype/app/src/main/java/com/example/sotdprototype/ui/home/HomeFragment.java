package com.example.sotdprototype.ui.home;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.sotdprototype.AuthorizeActivity;
import com.example.sotdprototype.MainActivity;
import com.example.sotdprototype.R;
import com.example.sotdprototype.Track;
import com.example.sotdprototype.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel mHomeViewModel;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHomeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        // Set song of the day
        mHomeViewModel.setTrack(((MainActivity) getActivity()).getTrack("66HVu3CZHOdLw9uYmftsfg"));
        //
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

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Track track = mHomeViewModel.getTrack().getValue();

        // TODO: Look for a potentially better way to use URI from HomeViewModel in conjunction with setOnClickListener
        final ImageButton buttonPlay = binding.buttonPlay;
        buttonPlay.setOnClickListener(v -> ((MainActivity) getActivity()).remotePlay(track.getUri()));

        final Button buttonOpenTrackInSpotify = binding.buttonOpenTrackInSpotify;
        buttonOpenTrackInSpotify.setOnClickListener(v -> ((MainActivity) getActivity()).openTrackInSpotify(track.getUri()));
        //

        // Refreshing data observed by Views
        mHomeViewModel.setTitleText(track.getTitle());
        mHomeViewModel.setArtistText(track.getArtist());
        mHomeViewModel.setSpotifyTrackURI(track.getUri());
        if (track.getCoverURL() != ""){
            Picasso.get()
                    .load(track.getCoverURL())
                    .error(R.drawable.placeholder_cover)
                    .into(binding.imageCover);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}