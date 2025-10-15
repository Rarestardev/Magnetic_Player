package com.rarestardev.magneticplayer.database.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rarestardev.magneticplayer.database.dao.FavoriteDao;
import com.rarestardev.magneticplayer.database.dao.MusicPlaylistDao;
import com.rarestardev.magneticplayer.database.dao.PlaylistDao;
import com.rarestardev.magneticplayer.database.dao.RecentListDao;
import com.rarestardev.magneticplayer.database.dao.SearchDao;
import com.rarestardev.magneticplayer.database.entities.MusicOnPlayListEntity;
import com.rarestardev.magneticplayer.database.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.database.entities.SearchEntity;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.RecentList;

@Database(entities = {MusicFile.class, RecentList.class, SearchEntity.class,
        PlaylistEntity.class, MusicOnPlayListEntity.class}, version = 2, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();

    public abstract RecentListDao recentListDao();

    public abstract SearchDao searchDao();

    public abstract PlaylistDao playlistDao();

    public abstract MusicPlaylistDao musicPlaylistDao();
}
