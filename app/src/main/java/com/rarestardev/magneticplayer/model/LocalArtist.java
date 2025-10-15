package com.rarestardev.magneticplayer.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "artist")
public class LocalArtist {

    @PrimaryKey
    private int id;
    private String artistName;
    private String artistCover;
    private int songCount;

    @Ignore
    public LocalArtist() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
