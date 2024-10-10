package com.example.sotdprototype.ui.history;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sotdprototype.R;
import com.example.sotdprototype.data.db.Track;
import com.example.sotdprototype.spotify.SpotifyHelper;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Track[] localDataSet;

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
     * @param dataSet Track[] containing the data that will be used to populate views to be used by RecyclerView
     */
    public HistoryAdapter(Track[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(
                (position+1) + " days ago: " + "\n"
                        + "Title: " + localDataSet[position].getTitle() + "\n"
                        + "Album: " + localDataSet[position].getAlbum() + "\n"
                        + "Artist: " + localDataSet[position].getArtist() + "\n"
        );

        viewHolder.getButton().setOnClickListener(view -> SpotifyHelper.openTrackInSpotify(localDataSet[position].getUri(), HistoryFragment.PACKAGE_NAME, HistoryFragment.CONTEXT));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}
