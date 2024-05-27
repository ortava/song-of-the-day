package com.example.sotdprototype.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sotdprototype.R;
import com.example.sotdprototype.spotify.SpotifyWebAPICommunicator;
import com.example.sotdprototype.data.db.TrackService;
import com.example.sotdprototype.databinding.FragmentHomeBinding;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

public class HomeFragment extends Fragment {
    private static final String CLIENT_ID = "b1a4a0e63d4745198ab789e13e42314d";
    private static final String REDIRECT_URI = "http://localhost:3000";

    private FragmentHomeBinding binding;
    private HomeViewModel mHomeViewModel;
    private TrackService mTrackService;
    private SpotifyWebAPICommunicator mSpotifyWebAPICommunicator;

    private ImageButton mImageButtonPlay;
    private SeekBar mSeekBarPlaytime;
    private TextView mTextViewPlaytimeLeft;
    private TextView mTextViewPlaytimeRight;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        mTrackService = new TrackService(requireContext());
        mSpotifyWebAPICommunicator = new SpotifyWebAPICommunicator(requireContext());

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

        //final TextView mTextViewPlaytimeLeft = binding.textPlaytimeLeft;
        mTextViewPlaytimeLeft = binding.textPlaytimeLeft;

        //final TextView mTextViewPlaytimeRight = binding.textPlaytimeRight;
        mTextViewPlaytimeRight = binding.textPlaytimeRight;
        //TODO: Make observable data that stores max duration as minutes and seconds.

        mSeekBarPlaytime = binding.seekbarPlaytime;
        mHomeViewModel.getDuration().observe(getViewLifecycleOwner(), mSeekBarPlaytime::setMax);
        mSeekBarPlaytime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTextViewPlaytimeLeft.setText(millisecondsToReadableTime(seekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SpotifyAppRemote remote = mHomeViewModel.getSpotifyAppRemote();
                remote.getPlayerApi().getPlayerState()
                        .setResultCallback(playerState -> {
                            // Only seek if the recommended track is playing in the user's Spotify app.
                            if(!playerState.track.uri.equals(mHomeViewModel.getSpotifyTrackURI().getValue())){
                                mSeekBarPlaytime.setProgress(0);
                            } else {
                                mHomeViewModel.getSpotifyAppRemote().getPlayerApi().seekTo(seekBar.getProgress());
                            }
                        }).setErrorCallback(throwable -> {
                            Log.e("HomeFragment", "Error getting PlayerState");
                        });
            }
        });

        mImageButtonPlay = binding.buttonPlay;

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
        mSpotifyWebAPICommunicator.getRecommendation(() -> {
            Log.d("HomeFragment", "GOT RECOMMENDATION");
            mHomeViewModel.setTrack(mSpotifyWebAPICommunicator.getSongOfTheDay());
            mTrackService.addTrackToDataBase(mSpotifyWebAPICommunicator.getSongOfTheDay());
        });
    }

    private void remotePlay(String uri) {
        SpotifyAppRemote remote = mHomeViewModel.getSpotifyAppRemote();
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
                    Log.e("HomeFragment", "Error getting PlayerState");
                });
    }

    private void openTrackInSpotify(String uri) {
        final String spotifyContent = uri;
        final String branchLink = "https://spotify.link/content_linking?~campaign=" + requireContext().getPackageName() + "&$deeplink_path=" + spotifyContent + "&$fallback_url=" + spotifyContent;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(branchLink));
        startActivity(intent);
    }

    private void connectSpotifyAppRemote() {
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
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("HomeFragment", throwable.getMessage(), throwable);
                    }
                });
    }

    private void connected() {
        // Set onClickListener for the Play button.
        mImageButtonPlay.setOnClickListener(v -> mHomeViewModel.getSpotifyTrackURI().observe(getViewLifecycleOwner(), this::remotePlay));

        // Set up thread for updating Seekbar progress.
        Handler handler = new Handler();
        Runnable seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                SpotifyAppRemote remote = mHomeViewModel.getSpotifyAppRemote();
                remote.getPlayerApi().getPlayerState()
                        .setResultCallback(playerState -> {
                            // Only update value if the recommended track is playing in the user's Spotify app.
                            if(!playerState.track.uri.equals(mHomeViewModel.getSpotifyTrackURI().getValue())){
                                mSeekBarPlaytime.setProgress(0);
                            } else {
                                mSeekBarPlaytime.setProgress((int) playerState.playbackPosition);
                            }
                        }).setErrorCallback(throwable -> {
                            Log.e("HomeFragment", "Error getting PlayerState");
                        });

                handler.postDelayed(this, 1000);    // Runnable calls itself every 1 second.
            }
        };

        handler.postDelayed(seekBarRunnable, 0);      // Start thread to automatically update seekbar progress.
    }

    // Takes a value in milliseconds and transforms it into a readable time format (minutes:seconds).
    private String millisecondsToReadableTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / 1000) / 60;
        return (seconds < 10)
                ? minutes + ":0" + seconds
                : minutes + ":" + seconds;
    }
}