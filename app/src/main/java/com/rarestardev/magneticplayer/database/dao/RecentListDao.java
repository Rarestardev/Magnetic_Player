package com.rarestardev.magneticplayer.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rarestardev.magneticplayer.model.RecentList;

import java.util.List;

@Dao
public interface RecentListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecentMusic(RecentList recentList);

    @Query("SELECT * FROM recent_list ORDER BY musicFiles_dateAdded DESC ")
    LiveData<List<RecentList>> getAllRecentList();

    @Query("UPDATE recent_list SET musicFiles_dateAdded = :dateAdded WHERE musicFiles_filePath = :filePath")
    void updateDateAdded(String filePath, long dateAdded);

    @Query("SELECT * FROM recent_list WHERE musicFiles_filePath = :filePath LIMIT 1")
    RecentList findByFileName(String filePath);

    @Query("UPDATE recent_list SET playCount = :playCount WHERE musicFiles_filePath = :filePath")
    void updatePLayCount(String filePath, long playCount);

    @Query("SELECT playCount FROM recent_list WHERE musicFiles_filePath = :filePath LIMIT 1")
    long playCountValue(String filePath);

    @Delete
    void deleteHistoryItemOnList(RecentList recentList);

    @Query("DELETE FROM recent_list")
    void deleteAllHistory();
}
