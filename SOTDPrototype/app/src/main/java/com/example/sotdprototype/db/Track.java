package com.example.sotdprototype.db;

import androidx.room.*;

@Entity
public class Track {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "spotify_Id")
    private String spotifyId;

    @ColumnInfo(name = "uri")
    private String uri;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "album")
    private String album;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "cover_URL")
    private String coverURL;

    public Track() {
        this.spotifyId = "";
        this.uri = "";
        this.title = "";
        this.album = "";
        this.artist = "";
        this.coverURL = "";
    }

    public Track(String spotifyId, String uri, String title, String album, String artist, String coverURL) {
        this.spotifyId = spotifyId;
        this.uri = uri;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverURL = coverURL;
    }

    public int getId() { return this.id; }
    public String getSpotifyId() {
        return this.spotifyId;
    }
    public String getUri() {
        return uri;
    }
    public String getTitle() {
        return this.title;
    }
    public String getAlbum() {
        return this.album;
    }
    public String getArtist() {
        return this.artist;
    }
    public String getCoverURL() { return this.coverURL; }


    public void setId(int id) { this.id = id; }
    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setCoverURL(String coverURL) { this.coverURL = coverURL; }
    public void setAll(Track otherTrack) {
        this.spotifyId = otherTrack.getSpotifyId();
        this.uri = otherTrack.getUri();
        this.title = otherTrack.getTitle();
        this.album = otherTrack.getAlbum();
        this.artist = otherTrack.getArtist();
        this.coverURL = otherTrack.getCoverURL();
    }
}
