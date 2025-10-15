package com.rarestardev.magneticplayer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rarestardev.magneticplayer.database.entities.SearchEntity;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.repositories.SearchMusicRepository;

import java.util.List;

public class SearchMusicViewModel extends AndroidViewModel {

    private final SearchMusicRepository repository;

    public SearchMusicViewModel(@NonNull Application application) {
        super(application);

        repository = new SearchMusicRepository(application);
    }

    public void search(String query){
        repository.searchMusic(query);
    }

    public LiveData<List<MusicFile>> searchResult(){
        return repository.getResult();
    }

    public void insertSearchTypes(SearchEntity searchEntity){
        repository.insertSearchType(searchEntity);
    }

    public LiveData<List<SearchEntity>> getAllRecentSearch(){
        return repository.getRecentSearch();
    }

    public void deleteAllRecentSearch(){
        repository.deleteAllRecentSearch();
    }
}
