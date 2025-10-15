package com.rarestardev.magneticplayer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "playlist")
public class PlaylistEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlistId")
    private int playlistId;

    @ColumnInfo(name = "playlistName")
    private String playlistName;

    @ColumnInfo(name = "current_date")
    private long current_date;

    public PlaylistEntity() {
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public long getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(long current_date) {
        this.current_date = current_date;
    }
}
