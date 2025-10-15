package com.rarestardev.magneticplayer.repositories;

import android.content.Context;

import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.MusicFolder;
import com.rarestardev.magneticplayer.utilities.MusicUtils;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class FoldersRepository {

    private final Context context;

    public FoldersRepository(Context context) {
        this.context = context;
    }

    public Observable<List<MusicFolder>> loadMusicFolder(){
        return Observable.create(emitter -> {
            List<MusicFolder> musicFolders = MusicUtils.getMusicFolders(context);

            emitter.onNext(musicFolders);
            emitter.onComplete();
        });
    }

    public Observable<List<MusicFile>> loadMusicOnFolder(String folderPath){
        return Observable.create(emitter -> {
            List<MusicFile> musicFiles = MusicUtils.getMusicFilesFromFolder(context,folderPath);

            emitter.onNext(musicFiles);
            emitter.onComplete();
        });
    }
}
