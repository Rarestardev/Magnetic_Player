package com.rarestardev.magneticplayer.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.rarestardev.magneticplayer.dao.RecentListDao;
import com.rarestardev.magneticplayer.database.AppDatabase;
import com.rarestardev.magneticplayer.database.DatabaseClient;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.RecentList;

import java.util.List;

public class RecentListRepository {

    private final RecentListDao recentListDao;

    public RecentListRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        recentListDao = db.recentListDao();
    }

    public void insertOrUpdateRecentList(MusicFile musicFile) {
        DatabaseClient.databaseWriteExecutor.execute(() -> {
            RecentList existingList = recentListDao.findByFileName(musicFile.getFilePath());
            if (existingList != null) {
                existingList.setMusicFiles(musicFile);
                updateDateAdded(musicFile.getFilePath(), System.currentTimeMillis());
            } else {
                RecentList newList = new RecentList(musicFile);
                recentListDao.insertRecentMusic(newList);
            }
        });
    }

    private void updateDateAdded(String filePath, long dateAdded) {
        DatabaseClient.databaseWriteExecutor.execute(() ->  recentListDao.updateDateAdded(filePath, dateAdded));
    }

    public LiveData<List<RecentList>> getAllRecentLists() {
        return recentListDao.getAllRecentList();
    }
}
