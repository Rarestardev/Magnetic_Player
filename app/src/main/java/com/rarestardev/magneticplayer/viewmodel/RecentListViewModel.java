package com.rarestardev.magneticplayer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.RecentList;
import com.rarestardev.magneticplayer.repositories.RecentListRepository;

import java.util.ArrayList;
import java.util.List;

public class RecentListViewModel extends AndroidViewModel {

    private final RecentListRepository recentListRepository;
    private final LiveData<List<RecentList>> allRecentLists;

    public RecentListViewModel(@NonNull Application application) {
        super(application);
        recentListRepository = new RecentListRepository(application);
        allRecentLists = recentListRepository.getAllRecentLists();
    }

    public LiveData<List<RecentList>> getAllRecentLists() {
        return allRecentLists;
    }

    public void insertOrUpdateRecentList(MusicFile musicFile) {
        recentListRepository.insertOrUpdateRecentList(musicFile);
    }

    public List<MusicFile> convertToMusicFileList(List<RecentList> recentList) {
        List<MusicFile> musicFileList = new ArrayList<>();
        for (RecentList recent : recentList) {
            musicFileList.add(recent.getMusicFiles());
        }
        return musicFileList;
    }
}
