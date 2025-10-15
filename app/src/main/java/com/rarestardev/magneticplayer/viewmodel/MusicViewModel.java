package com.rarestardev.magneticplayer.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.repositories.MusicRepository;
import com.rarestardev.magneticplayer.utilities.Constants;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicViewModel extends AndroidViewModel {

    private final MutableLiveData<List<MusicFile>> musicLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MusicRepository repository;

    public MusicViewModel(Application application) {
        super(application);
        this.repository = new MusicRepository(application);
    }

    public LiveData<List<MusicFile>> getAllMusic(){
        return musicLiveData;
    }

    public void loadMusic(){
        disposable.add(
                repository.loadMusic()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                musicLiveData::setValue,
                                throwable -> Log.e(Constants.appLog,"Error loading music",throwable)
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}

