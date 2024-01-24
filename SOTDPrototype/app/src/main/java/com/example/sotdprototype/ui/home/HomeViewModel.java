package com.example.sotdprototype.ui.home;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sotdprototype.R;
import com.example.sotdprototype.Track;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mHomeText;
    private final MutableLiveData<String> mTitleText;
    private final MutableLiveData<String> mArtistText;
    private final MutableLiveData<Integer> mCoverArtResource;
    private final MutableLiveData<String> mSpotifyTrackURI;
    private final MutableLiveData<Track> mTrack;

    public HomeViewModel() {
        mHomeText = new MutableLiveData<>();
        mHomeText.setValue("Welcome!");

        mTitleText = new MutableLiveData<>();
        mTitleText.setValue("Song Title");

        mArtistText = new MutableLiveData<>();
        mArtistText.setValue("Artist Name");

        mCoverArtResource = new MutableLiveData<>();
        mCoverArtResource.setValue(R.drawable.placeholder_cover);

        mSpotifyTrackURI = new MutableLiveData<>();
        mSpotifyTrackURI.setValue("spotify:track:66HVu3CZHOdLw9uYmftsfg");

        mTrack = new MutableLiveData<>();
        mTrack.setValue(new Track());
    }

    public void setHomeText(String homeText) { mHomeText.setValue(homeText); }
    public void setTitleText(String titleText) { mTitleText.setValue(titleText); }
    public void setArtistText(String artistText) { mArtistText.setValue(artistText); }
    //public void setCoverArtResource(Integer coverArtResource) { mCoverArtResource.setValue(coverArtResource); }
    public void setSpotifyTrackURI(String spotifyTrackURI) { mSpotifyTrackURI.setValue(spotifyTrackURI); }
    public void setTrack(Track track) {
        mTrack.setValue(track);
        setTitleText(track.getTitle());
        setArtistText(track.getArtist());
        setSpotifyTrackURI(track.getUri());
    }

    public LiveData<String> getHomeText() {
        return mHomeText;
    }
    public LiveData<String> getTitleText() {
        return mTitleText;
    }
    public LiveData<String> getArtistText() {
        return mArtistText;
    }
    public LiveData<Integer> getCoverImageResource() { return mCoverArtResource; }
    public LiveData<String> getSpotifyTrackURI() { return mSpotifyTrackURI; }
    public LiveData<Track> getTrack() { return mTrack; }
}