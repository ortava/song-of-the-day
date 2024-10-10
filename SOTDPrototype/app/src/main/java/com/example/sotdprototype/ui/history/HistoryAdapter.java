package com.example.sotdprototype.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sotdprototype.R;
import com.example.sotdprototype.spotify.SpotifyHelper;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private String[] localDataSet;
    private String[] localTrackURIs;

    /**
    Custom ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final Button buttonOpen;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textView);
            buttonOpen = (Button) view.findViewById(R.id.button_open);
        }

        public TextView getTextView() {
            return textView;
        }
        public Button getButton() { return buttonOpen; }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView
     */
    public HistoryAdapter(String[] dataSet, String[] trackURIs) {
        localDataSet = dataSet;
        localTrackURIs = trackURIs;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet[position]);
        viewHolder.getButton().setOnClickListener(view -> SpotifyHelper.openTrackInSpotify(localTrackURIs[position], HistoryFragment.PACKAGE_NAME, HistoryFragment.CONTEXT));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}
