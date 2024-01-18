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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sotdprototype.AuthorizeActivity;
import com.example.sotdprototype.MainActivity;
import com.example.sotdprototype.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewHome = binding.textHome;
        homeViewModel.getHomeText().observe(getViewLifecycleOwner(), textViewHome::setText);

        final TextView textViewTitle = binding.textTitle;
        homeViewModel.getTitleText().observe(getViewLifecycleOwner(), textViewTitle::setText);

        final TextView textViewArtist = binding.textArtist;
        homeViewModel.getArtistText().observe(getViewLifecycleOwner(), textViewArtist::setText);

        final ImageView imageViewCover = binding.imageCover;
        homeViewModel.getCoverImageResource().observe(getViewLifecycleOwner(), imageViewCover::setImageResource);

        // TODO: Look for a potentially better way to use URI from HomeViewModel in conjunction with setOnClickListener
        final ImageButton buttonPlay = binding.buttonPlay;
        buttonPlay.setOnClickListener(v -> ((MainActivity) getActivity()).remotePlay(homeViewModel.getSpotifyTrackURI().getValue()));
        
        final Button buttonOpenTrackInSpotify = binding.buttonOpenTrackInSpotify;
        buttonOpenTrackInSpotify.setOnClickListener(v -> ((MainActivity) getActivity()).openTrackInSpotify(homeViewModel.getSpotifyTrackURI().getValue()));
        //

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}