package com.example.sotdprototype.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sotdprototype.MainActivity;
import com.example.sotdprototype.R;
import com.example.sotdprototype.Track;
import com.example.sotdprototype.TrackService;
import com.example.sotdprototype.databinding.FragmentHistoryBinding;
import com.example.sotdprototype.ui.home.HomeViewModel;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private HistoryViewModel mHistoryViewModel;
    private TrackService mTrackService;

    protected RecyclerView mRecyclerView;
    protected HistoryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackService = new TrackService(getContext());

        mHistoryViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

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
        mHistoryViewModel.getDataSet().observe(getViewLifecycleOwner(), this::updateDataSet);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateDataSet(@NonNull String[] dataSet) {
        mAdapter = new HistoryAdapter(dataSet);
        mRecyclerView.swapAdapter(mAdapter, false);
    }

    private void initDataset() {
        // TODO: Either get multiple tracks via an array of IDs, (note that Volley responds asynchronously)
        // TODO: or retrieve all data from some sort of persistent storage (if possible, probably ideal).
        mTrackService.getTrackById("66HVu3CZHOdLw9uYmftsfg", () -> {
            mHistoryViewModel.setDataSet(mTrackService.makeDataSet(mHistoryViewModel.getDatasetCount()));
        });

        mTrackService.getTrackById("6knzYloG0x3MroAhnLVLGe", () -> {
            mHistoryViewModel.setDataSet(mTrackService.makeDataSet(mHistoryViewModel.getDatasetCount()));
        });
    }
}