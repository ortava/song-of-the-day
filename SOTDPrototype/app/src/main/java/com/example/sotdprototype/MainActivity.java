package com.example.sotdprototype;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.sotdprototype.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sotdprototype.databinding.ActivityMainBinding;


import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // temporary placement of Spotify connection fields
    private static final String CLIENT_ID = "b1a4a0e63d4745198ab789e13e42314d";
    private static final String REDIRECT_URI = "http://localhost:3000";
    private SpotifyAppRemote mSpotifyAppRemote;

    private TrackService mTrackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackService = new TrackService(getApplicationContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();

        connectSpotifyAppRemote();
    }

    public void remotePlay(String uri) {
        if(mSpotifyAppRemote != null){
            if(mSpotifyAppRemote.isConnected()){
                mSpotifyAppRemote.getPlayerApi().getPlayerState()
                        .setResultCallback(playerState -> {
                            if(!playerState.track.uri.equals(uri) && !uri.isEmpty()){
                                mSpotifyAppRemote.getPlayerApi().play(uri);
                                findViewById(R.id.button_play).setActivated(true);
                            }
                            else if(playerState.isPaused){
                                mSpotifyAppRemote.getPlayerApi().resume();
                                findViewById(R.id.button_play).setActivated(true);
                            } else {
                                mSpotifyAppRemote.getPlayerApi().pause();
                                findViewById(R.id.button_play).setActivated(false);
                            }
                        }).setErrorCallback(throwable -> {
                            Log.e("ERROR", "Error getting PlayerState");
                        });
            }
        }
    }

    public void openTrackInSpotify(String uri) {
        if(mSpotifyAppRemote != null){
            if(mSpotifyAppRemote.isConnected()){
                final String spotifyContent = uri;
                final String branchLink = "https://spotify.link/content_linking?~campaign=" + this.getPackageName() + "&$deeplink_path=" + spotifyContent + "&$fallback_url=" + spotifyContent;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(branchLink));
                startActivity(intent);
            }
        }
    }

    public void connectSpotifyAppRemote() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(false)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

}