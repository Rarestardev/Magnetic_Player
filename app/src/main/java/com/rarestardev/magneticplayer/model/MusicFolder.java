package com.rarestardev.magneticplayer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "musicFolder")
public class MusicFolder {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "folderName")
    private String folderName;

    @ColumnInfo(name = "folderPath")
    private String folderPath;

    @ColumnInfo(name = "numMusicFiles")
    private int numMusicFiles;

    // Constructor
    @Ignore
    public MusicFolder(String folderName, String folderPath, int numMusicFiles) {
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.numMusicFiles = numMusicFiles;
    }

    public MusicFolder() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters setter
    public String getFolderName() {
        return folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public int getNumMusicFiles() {
        return numMusicFiles;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void setNumMusicFiles(int numMusicFiles) {
        this.numMusicFiles = numMusicFiles;
    }
}

