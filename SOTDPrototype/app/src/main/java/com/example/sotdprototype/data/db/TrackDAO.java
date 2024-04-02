package com.example.sotdprototype.data.db;

import androidx.room.*;

import java.util.List;

@Dao
public interface TrackDAO {
    @Query("SELECT * FROM track")
    List<Track> getAll();

    @Query("SELECT COUNT(*) FROM track")
    int getCount();

    @Query("SELECT * FROM track WHERE spotify_Id = :spotify_Id")
    Track getTrackBySpotifyId(String spotify_Id);

    @Insert
    void insert(Track track);

    @Query("DELETE FROM track WHERE id=(SELECT MIN(id) FROM track)")
    void deleteTopRow();
}
