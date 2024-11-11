package com.sotd.ui.home;

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

import com.sotd.R;
import com.sotd.spotify.SpotifyHelper;
import com.sotd.spotify.SpotifyWebAPIService;
import com.sotd.data.db.TrackService;
import com.sotd.databinding.FragmentHomeBinding;
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
    private SpotifyWebAPIService mSpotifyWebAPIService;

    private ImageButton mImageButtonPlay;
    private SeekBar mSeekBarPlaytime;
    private TextView mTextViewPlaytimeLeft;
    private TextView mTextViewPlaytimeRight;

    private Handler handler;
    private Runnable seekBarRunnable;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        mTrackService = new TrackService(requireContext());
        mSpotifyWebAPIService = new SpotifyWebAPIService(requireContext());

        // Set up thread for updating Seekbar progress.
        handler = new Handler();
        seekBarRunnable = new Runnable() {  // TODO: Make runnable implementation more visually pleasing (make separate class?)
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

        // Retrieve new Song of the Day.
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

        mTextViewPlaytimeLeft = binding.textPlaytimeLeft;
        mHomeViewModel.getPlaytimeLeftText().observe(getViewLifecycleOwner(), mTextViewPlaytimeLeft::setText);

        mTextViewPlaytimeRight = binding.textPlaytimeRight;
        mHomeViewModel.getPlaytimeRightText().observe(getViewLifecycleOwner(), mTextViewPlaytimeRight::setText);

        mSeekBarPlaytime = binding.seekbarPlaytime;
        mHomeViewModel.getDuration().observe(getViewLifecycleOwner(), mSeekBarPlaytime::setMax);
        mSeekBarPlaytime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mHomeViewModel.setPlayTimeLeftText(seekBar.getProgress());
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
        mButtonOpenTrackInSpotify.setOnClickListener(v -> SpotifyHelper.openTrackInSpotify(
                    mHomeViewModel.getSpotifyTrackURI().getValue(),
                    requireContext().getPackageName(),
                    requireContext()
            ));

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
        handler.removeCallbacks(seekBarRunnable);
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
        mSpotifyWebAPIService.getRecommendation(() -> {
            Log.d("HomeFragment", "GOT RECOMMENDATION");
            mHomeViewModel.setTrack(mSpotifyWebAPIService.getSongOfTheDay());
            mTrackService.addTrackToDataBase(mSpotifyWebAPIService.getSongOfTheDay());
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

        // Start thread to automatically update seekbar progress.
        handler.postDelayed(seekBarRunnable, 0);
    }
}