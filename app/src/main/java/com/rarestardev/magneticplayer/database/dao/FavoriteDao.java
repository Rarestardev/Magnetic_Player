package com.rarestardev.magneticplayer.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTrack(MusicFile musicFile);

    @Query("SELECT * FROM music_tracks ORDER BY dateAdded DESC ")
    List<MusicFile> getAllFavoriteList();

    @Query("SELECT filePath FROM music_tracks WHERE filePath = :filePath LIMIT 1")
    LiveData<String> getFilePath(String filePath);

    @Delete
    void deleteFavoriteMusic(MusicFile musicFile);
}
