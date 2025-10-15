package com.rarestardev.magneticplayer.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.MusicFolder;
import com.rarestardev.magneticplayer.repositories.FoldersRepository;
import com.rarestardev.magneticplayer.utilities.Constants;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FolderViewModel extends AndroidViewModel {

    private final MutableLiveData<List<MusicFolder>> folderLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<MusicFile>> musicLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final FoldersRepository repository;

    public FolderViewModel(@NonNull Application application) {
        super(application);

        this.repository = new FoldersRepository(application);
    }

    public LiveData<List<MusicFolder>> getAllFolder(){
        return folderLiveData;
    }

    public LiveData<List<MusicFile>> getAllMusicOnFolder(){
        return musicLiveData;
    }

    public void loadFolder(){
        disposable.add(
                repository.loadMusicFolder()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                folderLiveData::setValue,
                                throwable -> Log.e(Constants.appLog,"Error loading folder",throwable)
                        )
        );
    }

    public void loadFolderOnMusic(String folderPath){
        disposable.add(
                repository.loadMusicOnFolder(folderPath)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                musicLiveData::setValue,
                                throwable -> Log.e(Constants.appLog,"Error loading music on folder",throwable)
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
