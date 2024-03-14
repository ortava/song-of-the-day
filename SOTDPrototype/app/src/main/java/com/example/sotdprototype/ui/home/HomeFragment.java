package com.example.sotdprototype.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sotdprototype.AppDatabase;
import com.example.sotdprototype.R;
import com.example.sotdprototype.Track;
import com.example.sotdprototype.TrackDAO;
import com.example.sotdprototype.TrackService;
import com.example.sotdprototype.databinding.FragmentHomeBinding;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    private static final String CLIENT_ID = "b1a4a0e63d4745198ab789e13e42314d";
    private static final String REDIRECT_URI = "http://localhost:3000";

    private FragmentHomeBinding binding;
    private HomeViewModel mHomeViewModel;
    private TrackService mTrackService;
    private TrackDAO trackDAO;

    private ImageButton mImageButtonPlay;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        mTrackService = new TrackService(requireContext());
        /// Testing DB stuff
        AppDatabase db = AppDatabase.getDbInstance(requireContext());
        trackDAO = db.trackDAO();
        ///

        setSongOfTheDay();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView mTextViewHome = binding.textHome;
        mHomeViewModel.getHomeText().observe(getViewLifecycleOwner(), mTextViewHome::setText);

        final TextView mTextViewTitle = binding.textTitle;
        mHomeViewModel.getTitleText().observe(getViewLifecycleOwner(), mTextViewTitle::setText);

        final TextView mTextViewArtist = binding.textArtist;
        mHomeViewModel.getArtistText().observe(getViewLifecycleOwner(), mTextViewArtist::setText);

        mImageButtonPlay = binding.buttonPlay;
        mImageButtonPlay.setOnClickListener(v -> mHomeViewModel.getSpotifyTrackURI().observe(getViewLifecycleOwner(), this::remotePlay));

        final Button mButtonOpenTrackInSpotify = binding.buttonOpenTrackInSpotify;
        mButtonOpenTrackInSpotify.setOnClickListener(v -> mHomeViewModel.getSpotifyTrackURI().observe(getViewLifecycleOwner(), this::openTrackInSpotify));

        mHomeViewModel.getCoverURL().observe(getViewLifecycleOwner(), this::loadCoverImage);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        connectSpotifyAppRemote();
    }

    @Override
    public void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mHomeViewModel.getSpotifyAppRemote());
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
            Log.d("HomeFragment", "GOT RECOMMENDATION");
            mHomeViewModel.setTrack(mTrackService.getSongOfTheDay());
            if(trackDAO.getCount() >= 30) { // TODO: Find better location to store max DATASET_COUNT
                trackDAO.deleteTopRow();
            }
            trackDAO.insert(mTrackService.getSongOfTheDay());
        });
    }

    public void remotePlay(String uri) {
        SpotifyAppRemote remote = mHomeViewModel.getSpotifyAppRemote();
        if(remote != null){
            if(remote.isConnected()){
                remote.getPlayerApi().getPlayerState()
                        .setResultCallback(playerState -> {
                            if(!playerState.track.uri.equals(uri) && !uri.isEmpty()){
                                remote.getPlayerApi().play(uri);
                                mImageButtonPlay.setActivated(true);
                            }
                            else if(playerState.isPaused){
                                remote.getPlayerApi().resume();
                                mImageButtonPlay.setActivated(true);
                            } else {
                                remote.getPlayerApi().pause();
                                mImageButtonPlay.setActivated(false);
                            }
                        }).setErrorCallback(throwable -> {
                            Log.e("ERROR", "Error getting PlayerState");
                        });
            }
        }
    }

    public void openTrackInSpotify(String uri) {
        final String spotifyContent = uri;
        final String branchLink = "https://spotify.link/content_linking?~campaign=" + requireContext().getPackageName() + "&$deeplink_path=" + spotifyContent + "&$fallback_url=" + spotifyContent;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(branchLink));
        startActivity(intent);
    }

    public void connectSpotifyAppRemote() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(false)
                        .build();

        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mHomeViewModel.setSpotifyAppRemote(spotifyAppRemote);
                        Log.d("HomeFragment", "Connected! Yay!");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("HomeFragment", throwable.getMessage(), throwable);
                    }
                });
    }
}