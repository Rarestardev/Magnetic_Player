package com.rarestardev.magneticplayer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rarestardev.magneticplayer.entities.MusicOnPlayListEntity;

import java.util.List;

@Dao
public interface MusicPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTrackOnPlayList(MusicOnPlayListEntity music);

    @Query("SELECT * FROM music_playlist WHERE playlist_name = :playlistName ORDER BY date DESC")
    List<MusicOnPlayListEntity> getTracksOnPlaylist(String playlistName);

    @Query("SELECT * FROM music_playlist WHERE playlist_name = :playlistName AND tracks_filePath = :filePath LIMIT 1")
    MusicOnPlayListEntity getTracksOnPlaylistChecked(String playlistName, String filePath);

    @Query("DELETE FROM music_playlist WHERE playlist_name = :playlistName AND tracks_filePath= :filePath")
    void deleteTrackOnPlaylist(String playlistName,String filePath);

    @Query("DELETE FROM music_playlist WHERE playlist_name = :playlistName")
    void deleteAllTrackOnPlaylist(String playlistName);
}
