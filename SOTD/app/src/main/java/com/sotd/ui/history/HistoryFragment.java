package com.sotd.ui.history;

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

import com.sotd.data.db.TrackService;
import com.sotd.R;
import com.sotd.data.db.Track;
import com.sotd.databinding.FragmentHistoryBinding;

import java.util.List;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private HistoryViewModel mHistoryViewModel;
    private Track[] mDataSet;
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

        initDataSet();
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
        updateAdapterDataSet(mDataSet);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateAdapterDataSet(@NonNull Track[] dataSet) {
        mAdapter = new HistoryAdapter(dataSet);
        mRecyclerView.swapAdapter(mAdapter, false);
    }

    // Initializes the HistoryAdapter dataset as an array of all the tracks in the database
    // in reverse order (the most recently added appears first).
    private void initDataSet() {
        List<Track> tracks = mTrackService.getAllTracksFromDataBase();
        mDataSet = new Track[tracks.size()];
        for(int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(tracks.size() - 1 - i);
            mDataSet[i] = track;
        }
    }
}