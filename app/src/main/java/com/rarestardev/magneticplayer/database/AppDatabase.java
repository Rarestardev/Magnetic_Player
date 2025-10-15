package com.rarestardev.magneticplayer.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rarestardev.magneticplayer.dao.FavoriteDao;
import com.rarestardev.magneticplayer.dao.MusicPlaylistDao;
import com.rarestardev.magneticplayer.dao.PlaylistDao;
import com.rarestardev.magneticplayer.dao.RecentListDao;
import com.rarestardev.magneticplayer.dao.SearchDao;
import com.rarestardev.magneticplayer.entities.MusicOnPlayListEntity;
import com.rarestardev.magneticplayer.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.entities.SearchEntity;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.RecentList;

@Database(entities = {MusicFile.class, RecentList.class, SearchEntity.class,
        PlaylistEntity.class, MusicOnPlayListEntity.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();

    public abstract RecentListDao recentListDao();

    public abstract SearchDao searchDao();

    public abstract PlaylistDao playlistDao();

    public abstract MusicPlaylistDao musicPlaylistDao();
}
