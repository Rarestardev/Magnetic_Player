package com.rarestardev.magneticplayer.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rarestardev.magneticplayer.dao.SearchDao;
import com.rarestardev.magneticplayer.database.AppDatabase;
import com.rarestardev.magneticplayer.database.DatabaseClient;
import com.rarestardev.magneticplayer.entities.SearchEntity;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.MusicUtils;

import java.util.List;

public class SearchMusicRepository {

    private final MutableLiveData<List<MusicFile>> data;
    private final Context context;
    private final SearchDao searchDao;

    public SearchMusicRepository(Context context){
        this.context = context;

        data = new MutableLiveData<>();

        AppDatabase appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        searchDao = appDatabase.searchDao();
    }

    public void searchMusic(String query){
        data.setValue(MusicUtils.getMusicFilesFromSearch(context,query));
    }

    public LiveData<List<MusicFile>> getResult(){
        return data;
    }

    public void insertSearchType(SearchEntity searchEntity){
        DatabaseClient.databaseWriteExecutor.execute(() -> searchDao.insert(searchEntity));
    }

    public LiveData<List<SearchEntity>> getRecentSearch(){
        return searchDao.getAllRecentSearchType();
    }

    public void deleteAllRecentSearch(){
        DatabaseClient.databaseWriteExecutor.execute(searchDao::deleteAll);
    }
}
