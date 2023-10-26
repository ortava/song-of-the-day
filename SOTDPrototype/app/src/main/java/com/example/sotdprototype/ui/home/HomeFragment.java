package com.example.sotdprototype.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        
        final Button buttonOpenTrackInSpotify = binding.buttonOpenTrackInSpotify;
        //buttonOpenTrackInSpotify.setOnClickListener(v -> ((MainActivity) getActivity()).openTrackInSpotify("spotify:track:1D1sFcA13TLiLXmqHUFBXR"));
        buttonOpenTrackInSpotify.setOnClickListener(v -> ((MainActivity) getActivity()).getTrack("1D1sFcA13TLiLXmqHUFBXR"));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}