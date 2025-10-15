package com.rarestardev.magneticplayer.model;

public class AlbumInfo {

    private int id;

    private String albumName;

    private String artistName;

    private String albumArtUri;

    private int songCount;

    public AlbumInfo(String albumName, String artistName, String albumArtUri, int songCount) {
        this.albumName = albumName;
        this.artistName = artistName;
        this.albumArtUri = albumArtUri;
        this.songCount = songCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}

