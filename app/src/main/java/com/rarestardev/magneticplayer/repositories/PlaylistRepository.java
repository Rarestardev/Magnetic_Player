package com.rarestardev.magneticplayer.repositories;

import android.content.Context;
import android.util.Log;

import com.rarestardev.magneticplayer.dao.MusicPlaylistDao;
import com.rarestardev.magneticplayer.dao.PlaylistDao;
import com.rarestardev.magneticplayer.database.AppDatabase;
import com.rarestardev.magneticplayer.database.DatabaseClient;
import com.rarestardev.magneticplayer.entities.MusicOnPlayListEntity;
import com.rarestardev.magneticplayer.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class PlaylistRepository {

    private final PlaylistDao playlistDao;
    private final MusicPlaylistDao musicPlaylistDao;
    private boolean isMusicInPlaylist = false;

    public PlaylistRepository(Context context){
        AppDatabase appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        playlistDao = appDatabase.playlistDao();
        musicPlaylistDao = appDatabase.musicPlaylistDao();
    }


    public Observable<List<PlaylistEntity>> loadPlaylist(){
        return Observable.create(emitter -> {
            List<PlaylistEntity> playlistEntities = playlistDao.getAllPlayList();

            emitter.onNext(new ArrayList<>(playlistEntities));
            emitter.onComplete();
        });
    }


    public Observable<List<MusicOnPlayListEntity>> loadMusicOnPlaylist(String playlistName){
        return Observable.create(emitter -> {
            List<MusicOnPlayListEntity> music = musicPlaylistDao.getTracksOnPlaylist(playlistName);

            emitter.onNext(new ArrayList<>(music));
            emitter.onComplete();
        });
    }

    public void insertTracksToPlaylist(MusicOnPlayListEntity music) {
        DatabaseClient.databaseWriteExecutor.execute(() -> musicPlaylistDao.insertTrackOnPlayList(music));
    }

    public boolean checkMusicInPlaylist(String playlistName, MusicFile musicFile) {
        DatabaseClient.databaseWriteExecutor.execute(() -> {
            MusicOnPlayListEntity music = musicPlaylistDao.getTracksOnPlaylistChecked(playlistName, musicFile.getFilePath());
            if (music != null){
                if (music.getMusicFile().getFilePath().equals(musicFile.getFilePath())){
                    isMusicInPlaylist = true;
                    Log.d(Constants.appLog,"Music on playlist true");
                }else {
                    isMusicInPlaylist = false;
                    Log.d(Constants.appLog,"Music on playlist false ");
                }
            }else {
                isMusicInPlaylist = false;
                Log.d(Constants.appLog,"Music on playlist false");
            }
        });

        return isMusicInPlaylist;
    }

    public void deleteTracksOnPlaylist(String playlist,String filePath) {
        DatabaseClient.databaseWriteExecutor.execute(() -> musicPlaylistDao.deleteTrackOnPlaylist(playlist, filePath));
    }

    public void deleteAllTracksOnPlaylist(String playlistName) {
        DatabaseClient.databaseWriteExecutor.execute(() -> musicPlaylistDao.deleteAllTrackOnPlaylist(playlistName));
    }

    public void deletePlaylist(PlaylistEntity playlist) {
        DatabaseClient.databaseWriteExecutor.execute(() -> playlistDao.deletePlaylist(playlist));
    }
}
