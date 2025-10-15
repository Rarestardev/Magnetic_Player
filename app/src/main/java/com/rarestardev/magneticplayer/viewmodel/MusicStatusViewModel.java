package com.rarestardev.magneticplayer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.List;

public class MusicStatusViewModel extends ViewModel {

    private final MutableLiveData<MusicFile> musicInfo;
    private final MutableLiveData<Boolean> isPlayMusic;
    private final MutableLiveData<Integer> currentDurationMusic;
    private final MutableLiveData<List<MusicFile>> musicFiles;
    private final MutableLiveData<Integer> currentPosition;
    private final MutableLiveData<Long> currentMillisTimer;
    private final MutableLiveData<String> FilePath;
    private final MutableLiveData<Integer> audioSessionIdLive;

    public MusicStatusViewModel() {
        musicInfo = new MutableLiveData<>();
        isPlayMusic = new MutableLiveData<>();
        currentDurationMusic = new MutableLiveData<>();
        musicFiles = new MutableLiveData<>();
        currentPosition = new MutableLiveData<>();
        FilePath = new MutableLiveData<>();
        audioSessionIdLive = new MutableLiveData<>();
        currentMillisTimer = new MutableLiveData<>();
    }

    public LiveData<Integer> getAudioSessionIdLive() {
        return audioSessionIdLive;
    }

    public void setAudioSessionId(int sessionId) {
        audioSessionIdLive.setValue(sessionId);
    }

    public void setTimerMillis(long millis){
        currentMillisTimer.setValue(millis);
    }

    public LiveData<Long> getTimerMillis(){
        return currentMillisTimer;
    }


    public LiveData<MusicFile> getMusicInfo() {
        return musicInfo;
    }

    public void setMusicInfo(MusicFile info) {
        musicInfo.setValue(info);
    }

    public LiveData<Boolean> getIsPlayMusic() {
        return isPlayMusic;
    }

    public void setIsPlayMusic(boolean isPlay) {
        isPlayMusic.setValue(isPlay);
    }

    public LiveData<Integer> getDurationMusic() {
        return currentDurationMusic;
    }

    public void setCurrentDurationMusic(int currentDuration) {
        currentDurationMusic.setValue(currentDuration);
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        currentPosition.setValue(position);
    }

    public void setMusicFilesQueue(List<MusicFile> musicFilesQueue) {
        musicFiles.setValue(musicFilesQueue);
    }

    public LiveData<List<MusicFile>> getQueueList() {
        return musicFiles;
    }

    public void setFilePath(String s) {
        FilePath.setValue(s);
    }

    public LiveData<String> getFilePath() {
        return FilePath;
    }
}
