package com.sotd.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sotd.data.db.Track;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mHomeText;
    private final MutableLiveData<String> mTitleText;
    private final MutableLiveData<String> mArtistText;
    private final MutableLiveData<String> mSpotifyTrackURI;
    private final MutableLiveData<String> mCoverURL;
    private final MutableLiveData<Integer> mDuration;
    private final MutableLiveData<Track> mTrack;
    private final MutableLiveData<String> mPlayTimeLeftText;
    private final MutableLiveData<String> mPlayTimeRightText;
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

        mDuration = new MutableLiveData<>();
        mDuration.setValue(0);

        mSpotifyTrackURI = new MutableLiveData<>();
        mSpotifyTrackURI.setValue("");

        mTrack = new MutableLiveData<>();
        mTrack.setValue(new Track());

        mPlayTimeLeftText = new MutableLiveData<>();
        mPlayTimeLeftText.setValue("");

        mPlayTimeRightText = new MutableLiveData<>();
        mPlayTimeRightText.setValue("");
    }

    public void setHomeText(String homeText) { mHomeText.setValue(homeText); }
    public void setTitleText(String titleText) { mTitleText.setValue(titleText); }
    public void setArtistText(String artistText) { mArtistText.setValue(artistText); }
    public void setSpotifyTrackURI(String spotifyTrackURI) { mSpotifyTrackURI.setValue(spotifyTrackURI); }
    public void setCoverURL(String coverArtURL) { mCoverURL.setValue(coverArtURL); }
    public void setDuration(int duration) {
        mDuration.setValue(duration);

        setPlayTimeRightText(duration); // Violates Single-Responsibility Principle...
        // temp solution because I can't find a better place to set right text after ensuring duration is retrieved.
    }
    public void setTrack(Track track) {
        mTrack.setValue(track);
        setTitleText(track.getTitle());
        setArtistText(track.getArtist());
        setSpotifyTrackURI(track.getUri());
        setCoverURL(track.getCoverURL());
        setDuration(track.getDuration());
    }
    public void setPlayTimeLeftText(int currentPlayTime){ mPlayTimeLeftText.setValue(millisecondsToReadableTime(currentPlayTime)); }
    public void setPlayTimeRightText(int duration){ mPlayTimeRightText.setValue(millisecondsToReadableTime(duration)); }
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
    public LiveData<Integer> getDuration() { return mDuration; }
    public LiveData<Track> getTrack() { return mTrack; }
    public LiveData<String> getPlaytimeLeftText() { return mPlayTimeLeftText; }
    public LiveData<String> getPlaytimeRightText() { return mPlayTimeRightText; }
    public SpotifyAppRemote getSpotifyAppRemote() { return mSpotifyAppRemote; }

    /// HELPER METHODS
    // Takes a value in milliseconds and transforms it into a readable time format (minutes:seconds).
    private String millisecondsToReadableTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / 1000) / 60;
        return (seconds < 10)
                ? minutes + ":0" + seconds
                : minutes + ":" + seconds;
    }
}