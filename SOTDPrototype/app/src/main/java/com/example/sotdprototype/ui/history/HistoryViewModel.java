package com.example.sotdprototype.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HistoryViewModel extends ViewModel {
    private final MutableLiveData<String> mHistoryText;

    public HistoryViewModel() {
        mHistoryText = new MutableLiveData<>();
        mHistoryText.setValue("History");
    }

    public LiveData<String> getText() {
        return mHistoryText;
    }
}