package com.rarestardev.magneticplayer.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.repositories.FavoriteRepository;
import com.rarestardev.magneticplayer.utilities.Constants;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoriteMusicViewModel extends AndroidViewModel {

    private final FavoriteRepository repository;
    private final MutableLiveData<List<MusicFile>> allFavorites = new MutableLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();


    public FavoriteMusicViewModel(@NonNull Application application) {
        super(application);
        this.repository = new FavoriteRepository(application);
    }


    public LiveData<List<MusicFile>> getAllFavoriteList() {
        return allFavorites;
    }

    public LiveData<String> getFilePath(String filePath) {
        return repository.getFilePath(filePath);
    }

    public void loadFavoriteList(){
        disposable.add(
                repository.getAllFavoriteList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                allFavorites::setValue,
                                throwable -> Log.e(Constants.appLog,"Failed to load favorite list" + throwable)
                        )
        );
    }

    public void insertFavoriteData(MusicFile musicFile){
        repository.insertFavoriteMusic(musicFile);
    }

    public void deleteFavoriteMusic(MusicFile musicFile) {
        repository.deleteFavoriteMusic(musicFile);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

}
