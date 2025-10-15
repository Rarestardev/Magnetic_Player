package com.rarestardev.magneticplayer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_search")
public class SearchEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "searchId")
    private int searchId;

    @ColumnInfo(name = "word")
    private String word;

    @ColumnInfo(name = "dateAdded")
    private long dateAdded;

    public SearchEntity() {
    }


    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
