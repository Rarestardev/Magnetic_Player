package com.rarestardev.magneticplayer.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.rarestardev.magneticplayer.dao.FavoriteDao;
import com.rarestardev.magneticplayer.database.AppDatabase;
import com.rarestardev.magneticplayer.database.DatabaseClient;
import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class FavoriteRepository {

    private final FavoriteDao favoriteDao;

    public FavoriteRepository(Context context) {
        AppDatabase appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        favoriteDao = appDatabase.favoriteDao();

    }

    public Observable<List<MusicFile>> getAllFavoriteList() {
        return Observable.create(emitter -> {
            List<MusicFile> musicFiles = favoriteDao.getAllFavoriteList();

            emitter.onNext(new ArrayList<>(musicFiles));
            emitter.onComplete();
        });
    }

    public void insertFavoriteMusic(MusicFile musicFile) {
        DatabaseClient.databaseWriteExecutor.execute(() -> favoriteDao.insertTrack(musicFile));
    }

    public void deleteFavoriteMusic(MusicFile musicFile) {
        DatabaseClient.databaseWriteExecutor.execute(() -> favoriteDao.deleteFavoriteMusic(musicFile));
    }

    public LiveData<String> getFilePath(String filePath) {
        return favoriteDao.getFilePath(filePath);
    }
}
