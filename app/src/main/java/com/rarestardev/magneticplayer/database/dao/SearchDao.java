package com.rarestardev.magneticplayer.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rarestardev.magneticplayer.database.entities.SearchEntity;

import java.util.List;

@Dao
public interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SearchEntity searchEntity);

    @Query("SELECT * FROM recent_search ORDER BY dateAdded DESC")
    LiveData<List<SearchEntity>> getAllRecentSearchType();

    @Query("DELETE FROM recent_search")
    void deleteAll();
}
