package com.example.sotdprototype.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sotdprototype.Track;

import java.util.ArrayList;
import java.util.Queue;

public class HistoryViewModel extends ViewModel {

    private static final int DATASET_COUNT = 30;
    private final MutableLiveData<String> mHistoryText;
    private final MutableLiveData<String[]> mDataSet;

    public HistoryViewModel() {
        mHistoryText = new MutableLiveData<>();
        mHistoryText.setValue("History");

        mDataSet = new MutableLiveData<>();
        mDataSet.setValue(new String[DATASET_COUNT]);
    }

    public LiveData<String> getText() {
        return mHistoryText;
    }
    public LiveData<String[]> getDataSet() { return mDataSet; }
    public int getDatasetCount() { return DATASET_COUNT; }

    public void setDataSet(String[] dataSet) {
        mDataSet.setValue(dataSet);
    }
}