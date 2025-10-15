package com.rarestardev.magneticplayer.model;

public class ArtistMusicModel {

    private String artistName;
    private String artistCover;
    private int songCount;

    public ArtistMusicModel(String artistName, String artistCover, int songCount) {
        this.artistName = artistName;
        this.artistCover = artistCover;
        this.songCount = songCount;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistCover() {
        return artistCover;
    }

    public void setArtistCover(String artistCover) {
        this.artistCover = artistCover;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}
