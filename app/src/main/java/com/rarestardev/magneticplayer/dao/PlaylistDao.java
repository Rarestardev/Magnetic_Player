package com.rarestardev.magneticplayer.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rarestardev.magneticplayer.entities.PlaylistEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface PlaylistDao {

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    Completable insertPlaylist(PlaylistEntity playlist);

    @Query("SELECT * FROM playlist ORDER BY `current_date` DESC ")
    List<PlaylistEntity> getAllPlayList();

    @Delete
    void deletePlaylist(PlaylistEntity playlist);
}
