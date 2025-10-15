package com.rarestardev.magneticplayer.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rarestardev.magneticplayer.database.dao.PlaylistDao;
import com.rarestardev.magneticplayer.database.db.AppDatabase;
import com.rarestardev.magneticplayer.database.db.DatabaseClient;
import com.rarestardev.magneticplayer.database.entities.MusicOnPlayListEntity;
import com.rarestardev.magneticplayer.database.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.repositories.PlaylistRepository;
import com.rarestardev.magneticplayer.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlayListViewModel extends AndroidViewModel {

    private final PlaylistRepository repository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<List<PlaylistEntity>> playlistLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<MusicOnPlayListEntity>> musicOnPlaylistLiveData = new MutableLiveData<>();

    private final PlaylistDao playlistDao;

    public PlayListViewModel(@NonNull Application application) {
        super(application);
        this.repository = new PlaylistRepository(application);
        AppDatabase appDatabase = DatabaseClient.getInstance(application).getAppDatabase();
        playlistDao = appDatabase.playlistDao();
    }

    public LiveData<List<PlaylistEntity>> getAllPlaylist() {
        return playlistLiveData;
    }

    public void loadPlaylist() {
        disposable.add(
                repository.loadPlaylist()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                playlistLiveData::setValue,
                                throwable -> Log.e(Constants.appLog, "error loading playlist data")
                        )
        );
    }

    public LiveData<List<MusicOnPlayListEntity>> getMusicOnPlaylist(){
        return musicOnPlaylistLiveData;
    }

    public void loadMusicOnPlaylist(String playlistName){
        disposable.add(
                repository.loadMusicOnPlaylist(playlistName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                musicOnPlaylistLiveData::setValue,
                                throwable -> Log.e(Constants.appLog,"error loading music on playlist")
                        )
        );
    }

    public Completable insertPlaylist(PlaylistEntity playlist) {
        return playlistDao.insertPlaylist(playlist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean checkMusicInPlaylist(String playlistName, MusicFile musicFile) {
        return repository.checkMusicInPlaylist(playlistName, musicFile);
    }

    public void addedMusicIntoPlaylist(MusicFile musicFile, String playlistName) {
        MusicOnPlayListEntity musicOnPlayList = new MusicOnPlayListEntity();
        musicOnPlayList.setPlaylist_name(playlistName);
        musicOnPlayList.setMusicFile(musicFile);
        musicOnPlayList.setDate(System.currentTimeMillis());

        repository.insertTracksToPlaylist(musicOnPlayList);
    }

    public void deleteTracksOnPlaylist(String playlist,String filePath) {
        repository.deleteTracksOnPlaylist(playlist,filePath);
    }

    public void deleteAllTracksOnPlaylist(String playlistName) {
        repository.deleteAllTracksOnPlaylist(playlistName);
    }

    public void deletePlaylist(PlaylistEntity playlist) {
        repository.deletePlaylist(playlist);
    }

    public List<MusicFile> convertMusicToListFromPlaylist(List<MusicOnPlayListEntity> music) {
        List<MusicFile> musicFiles = new ArrayList<>();
        if (music != null) {
            for (MusicOnPlayListEntity musicOnPlayList : music) {
                musicFiles.add(musicOnPlayList.getMusicFile());
            }
        }
        return musicFiles;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
