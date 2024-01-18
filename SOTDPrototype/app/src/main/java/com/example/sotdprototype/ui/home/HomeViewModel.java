package com.example.sotdprototype.ui.home;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sotdprototype.R;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mHomeText;
    private final MutableLiveData<String> mTitleText;
    private final MutableLiveData<String> mArtistText;
    private final MutableLiveData<Integer> mCoverArtResource;
    private final MutableLiveData<String> mSpotifyTrackURI;

    public HomeViewModel() {
        mHomeText = new MutableLiveData<>();
        mHomeText.setValue("Welcome to SOTD! \n\n This is your song of the day:");

        mTitleText = new MutableLiveData<>();
        mTitleText.setValue("Song Title");

        mArtistText = new MutableLiveData<>();
        mArtistText.setValue("Artist Name");

        mCoverArtResource = new MutableLiveData<>();
        mCoverArtResource.setValue(R.drawable.placeholder_cover);

        mSpotifyTrackURI = new MutableLiveData<>();
        mSpotifyTrackURI.setValue("spotify:track:66HVu3CZHOdLw9uYmftsfg");
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
}