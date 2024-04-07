package com.example.sotdprototype.data.db;

import android.content.Context;
import java.util.List;

public class TrackService {
    public static final int MAX_DATASET_COUNT = 30;
    private static TrackDAO trackDAO;

    public TrackService(Context context) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        trackDAO = db.trackDAO();
    }

    public void addTrackToDataBase(Track track) {
        if(trackDAO.getCount() >= MAX_DATASET_COUNT) {
            trackDAO.deleteTopRow();
        }
        trackDAO.insert(track);
    }

    public List<Track> getAllTracksFromDataBase() {
        return trackDAO.getAll();
    }

    public boolean isInDatabase(String id) {
        return trackDAO.getTrackBySpotifyId(id) != null;
    }
}
