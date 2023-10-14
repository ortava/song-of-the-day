package com.example.sotdprototype.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sotdprototype.R;
import com.example.sotdprototype.Track;
import com.example.sotdprototype.TrackService;
import com.example.sotdprototype.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {
    private static final int DATASET_COUNT = 50;

    private FragmentHistoryBinding binding;

    protected RecyclerView mRecyclerView;
    protected HistoryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataSet;

    private TrackService mTrackService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTrackService = new TrackService(this.getContext());
        initDataset();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHistory;
        historyViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        // start RecyclerView stuff -
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view_history);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new HistoryAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // end RecyclerView stuff.

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initDataset() {
        mDataSet = new String[DATASET_COUNT];
        for(int i = 0; i < DATASET_COUNT; i++) {
            Track track = mTrackService.getTrack();
            mDataSet[i] = (i+1) + " days ago: " + "\n"
                    + "Title: " + track.getTitle() + "\n"
                    + "Album: " + track.getAlbum() + "\n"
                    + "Artist: " + track.getArtist() + "\n"
                    + "[open in Spotify]";
        }
    }
}