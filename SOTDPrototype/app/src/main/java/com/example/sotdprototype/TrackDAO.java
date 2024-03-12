package com.example.sotdprototype;

import androidx.room.*;

import java.util.List;

@Dao
public interface TrackDAO {
    @Query("SELECT * FROM track")
    List<Track> getAll();

    @Query("SELECT COUNT(*) FROM track")
    int getCount();

    @Insert
    void insert(Track track);

    @Query("DELETE FROM track WHERE id=(SELECT MAX(id) FROM track)")
    void deleteLast();
}
