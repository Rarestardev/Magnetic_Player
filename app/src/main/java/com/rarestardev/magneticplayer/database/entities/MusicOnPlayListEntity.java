package com.rarestardev.magneticplayer.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.rarestardev.magneticplayer.model.MusicFile;

@Entity(tableName = "music_playlist")
public class MusicOnPlayListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "musicId")
    private int musicId;

    @ColumnInfo(name = "playlist_name")
    private String playlist_name;

    @Embedded(prefix = "tracks_")
    private MusicFile musicFile;

    @ColumnInfo(name = "date")
    private long date;

    public MusicOnPlayListEntity() {
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public MusicFile getMusicFile() {
        return musicFile;
    }

    public void setMusicFile(MusicFile musicFile) {
        this.musicFile = musicFile;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
