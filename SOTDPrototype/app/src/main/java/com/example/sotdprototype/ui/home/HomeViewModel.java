package com.example.sotdprototype.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sotdprototype.data.db.Track;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mHomeText;
    private final MutableLiveData<String> mTitleText;
    private final MutableLiveData<String> mArtistText;
    private final MutableLiveData<String> mSpotifyTrackURI;
    private final MutableLiveData<String> mCoverURL;
    private final MutableLiveData<Track> mTrack;
    private SpotifyAppRemote mSpotifyAppRemote;

    public HomeViewModel() {
        mHomeText = new MutableLiveData<>();
        mHomeText.setValue("Welcome!");

        mTitleText = new MutableLiveData<>();
        mTitleText.setValue("");

        mArtistText = new MutableLiveData<>();
        mArtistText.setValue("");

        mCoverURL = new MutableLiveData<>();
        mCoverURL.setValue("");

        mSpotifyTrackURI = new MutableLiveData<>();
        mSpotifyTrackURI.setValue("");

        mTrack = new MutableLiveData<>();
        mTrack.setValue(new Track());
    }

    public void setHomeText(String homeText) { mHomeText.setValue(homeText); }
    public void setTitleText(String titleText) { mTitleText.setValue(titleText); }
    public void setArtistText(String artistText) { mArtistText.setValue(artistText); }
    public void setSpotifyTrackURI(String spotifyTrackURI) { mSpotifyTrackURI.setValue(spotifyTrackURI); }
    public void setCoverURL(String coverArtURL) { mCoverURL.setValue(coverArtURL); }
    public void setTrack(Track track) {
        mTrack.setValue(track);
        setTitleText(track.getTitle());
        setArtistText(track.getArtist());
        setSpotifyTrackURI(track.getUri());
        setCoverURL(track.getCoverURL());
    }
    public void setSpotifyAppRemote(SpotifyAppRemote remote) { mSpotifyAppRemote = remote; }

    public LiveData<String> getHomeText() {
        return mHomeText;
    }
    public LiveData<String> getTitleText() {
        return mTitleText;
    }
    public LiveData<String> getArtistText() {
        return mArtistText;
    }
    public LiveData<String> getSpotifyTrackURI() { return mSpotifyTrackURI; }
    public LiveData<String> getCoverURL() { return mCoverURL; }
    public LiveData<Track> getTrack() { return mTrack; }

    public SpotifyAppRemote getSpotifyAppRemote() { return mSpotifyAppRemote; }
}