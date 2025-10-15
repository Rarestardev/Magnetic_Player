package com.rarestardev.magneticplayer.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.rarestardev.magneticplayer.database.dao.RecentListDao;
import com.rarestardev.magneticplayer.database.db.AppDatabase;
import com.rarestardev.magneticplayer.database.db.DatabaseClient;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.RecentList;
import com.rarestardev.magneticplayer.utilities.Constants;

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
            long playCount = recentListDao.playCountValue(musicFile.getFilePath());
            if (existingList != null) {
                existingList.setMusicFiles(musicFile);
                updateDateAdded(musicFile.getFilePath(), System.currentTimeMillis(), playCount + 1);
            } else {
                RecentList newList = new RecentList(musicFile, 1);
                recentListDao.insertRecentMusic(newList);
            }
        });
    }

    private void updateDateAdded(String filePath, long dateAdded, long playCount) {
        DatabaseClient.databaseWriteExecutor.execute(() -> recentListDao.updateDateAdded(filePath, dateAdded));
        DatabaseClient.databaseWriteExecutor.execute(() -> {
            recentListDao.updatePLayCount(filePath, playCount);
            Log.d(Constants.appLog, "update play count");
        });
    }

    public LiveData<List<RecentList>> getAllRecentLists() {
        return recentListDao.getAllRecentList();
    }

    public void deleteHistoryListItem(RecentList recentList) {
        DatabaseClient.databaseWriteExecutor.execute(() -> recentListDao.deleteHistoryItemOnList(recentList));
    }

    public void deleteAllHistory() {
        DatabaseClient.databaseWriteExecutor.execute(recentListDao::deleteAllHistory);
    }
}
