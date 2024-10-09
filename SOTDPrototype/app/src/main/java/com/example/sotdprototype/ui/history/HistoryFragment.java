package com.example.sotdprototype.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sotdprototype.data.db.TrackService;
import com.example.sotdprototype.R;
import com.example.sotdprototype.data.db.Track;
import com.example.sotdprototype.databinding.FragmentHistoryBinding;

import java.util.List;

public class HistoryFragment extends Fragment {
    public static String PACKAGE_NAME;
    public static Context CONTEXT;

    private FragmentHistoryBinding binding;
    private HistoryViewModel mHistoryViewModel;
    private String[] mDataSet = new String[TrackService.MAX_DATASET_COUNT];
    private String[] mTrackURIs = new String[TrackService.MAX_DATASET_COUNT];
    private TrackService mTrackService;

    protected RecyclerView mRecyclerView;
    protected HistoryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHistoryViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);
        mTrackService = new TrackService(requireContext());

        PACKAGE_NAME = requireContext().getPackageName();
        CONTEXT = getContext();

        initDataset();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHistory;
        mHistoryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        mRecyclerView = root.findViewById(R.id.recycler_view_history);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        updateDataSet(mDataSet, mTrackURIs);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateDataSet(@NonNull String[] dataSet, @NonNull String[] URIs) {
        mAdapter = new HistoryAdapter(dataSet, URIs);
        mRecyclerView.swapAdapter(mAdapter, false);
    }

    private void initDataset() {
        List<Track> tracks = mTrackService.getAllTracksFromDataBase();

        for(int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(tracks.size() - 1 - i);
            if (i < mDataSet.length) {
                mDataSet[i] =
                        (i+1) + " days ago: " + "\n"
                        + "Title: " + track.getTitle() + "\n"
                        + "Album: " + track.getAlbum() + "\n"
                        + "Artist: " + track.getArtist() + "\n";
                mTrackURIs[i] = track.getUri();
            }
        }
    }
}