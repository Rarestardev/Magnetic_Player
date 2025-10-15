package com.rarestardev.magneticplayer.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_list")
public class RecentList {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @Embedded(prefix = "musicFiles_")
    private MusicFile musicFiles;

    public RecentList() {
    }


    @Ignore
    public RecentList(MusicFile musicFiles) {
        this.musicFiles = musicFiles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MusicFile getMusicFiles() {
        return musicFiles;
    }

    public void setMusicFiles(MusicFile musicFiles) {
        this.musicFiles = musicFiles;
    }
}
