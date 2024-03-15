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

import com.example.sotdprototype.db.AppDatabase;
import com.example.sotdprototype.R;
import com.example.sotdprototype.db.Track;
import com.example.sotdprototype.db.TrackDAO;
import com.example.sotdprototype.databinding.FragmentHistoryBinding;

import java.util.List;

public class HistoryFragment extends Fragment {
    private static final int DATASET_COUNT = 30;

    private FragmentHistoryBinding binding;
    private HistoryViewModel mHistoryViewModel;
    private String[] mDataSet = new String[DATASET_COUNT];
    private TrackDAO trackDAO;

    protected RecyclerView mRecyclerView;
    protected HistoryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHistoryViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        AppDatabase db = AppDatabase.getDbInstance(requireContext());
        trackDAO = db.trackDAO();

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
        updateDataSet(mDataSet);

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
        List<Track> tracks = trackDAO.getAll();

        for(int i = 0; i < mHistoryViewModel.getDatasetCount(); i++) {
            if (i < tracks.size()) {
                Track track = tracks.get(i);
                mDataSet[i] = (i+1) + " days ago: " + "\n"
                        + "Title: " + track.getTitle() + "\n"
                        + "Album: " + track.getAlbum() + "\n"
                        + "Artist: " + track.getArtist() + "\n"
                        + "[open in Spotify]";
            } else {
                break;
            }
        }
    }
}