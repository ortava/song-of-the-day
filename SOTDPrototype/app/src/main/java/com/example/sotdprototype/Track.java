package com.example.sotdprototype;

public class Track {
    private String id;
    private String uri;
    private String title;
    private String album;
    private String artist;

    public Track() {
        this.id = "";
        this.uri = "";
        this.title = "";
        this.album = "";
        this.artist = "";
    }

    public Track(String id, String uri, String title, String album, String artist) {
        this.id = id;
        this.uri = uri;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    public String getId() {
        return this.id;
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

    public void setId(String id) {
        this.id = id;
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
}