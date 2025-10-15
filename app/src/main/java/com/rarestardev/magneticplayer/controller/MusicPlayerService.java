package com.rarestardev.magneticplayer.controller;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.enums.NotificationAction;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.RecentListViewModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MusicPlayerService extends Service {

    private MediaPlayer mediaPlayer;
    private Map<Integer, MusicFile> musicFilesMap;
    private List<MusicFile> musicFiles;
    private MusicStatusViewModel musicStatusViewModel;
    private RecentListViewModel recentListViewModel;
    private final Handler handler = new Handler();
    private Runnable updateTimeTask;
    private MusicPlaybackSettings musicPlaybackSettings;
    private NotificationMusicController notificationMusicController;

    private boolean isShuffleEnabled;
    private boolean isRepeatEnabled;
    private int positionWhenShuffleDisabled;
    private int currentIndex = 0;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationMusicController = new NotificationMusicController(this);
        musicPlaybackSettings = new MusicPlaybackSettings(this);

        MusicApplication application = (MusicApplication) getApplication();
        musicStatusViewModel = application.getMusicViewModel();
        recentListViewModel = application.getRecentViewModel();

        isRepeatEnabled = musicPlaybackSettings.getIsRepeat();
        isShuffleEnabled = musicPlaybackSettings.getIsShuffle();

        mediaPlayer = new MediaPlayer();
        updateTimeTask = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    musicStatusViewModel.setCurrentDurationMusic(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateTimeTask);

        mediaPlayer.setOnCompletionListener(mp -> {
            if (isRepeatEnabled) {
                mp.start();
            } else {
                playNextMusic();
            }
        });

        notificationMusicController.setMediaPlayer(mediaPlayer);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(ExtraKey.MUSIC_LIST.getValue())) {
            musicFiles = intent.getParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue());
            musicFilesMap = new HashMap<>();
            for (int i = 0; i < musicFiles.size(); i++) {
                musicFilesMap.put(i, musicFiles.get(i));
            }
        }
        if (intent != null) {
            currentIndex = intent.getIntExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), -1);
        }
        isRepeatEnabled = musicPlaybackSettings.getIsRepeat();
        isShuffleEnabled = musicPlaybackSettings.getIsShuffle();

        playMusic(currentIndex);

        handleMusicAction(intent.getAction());

        return START_STICKY;
    }

    private void handleMusicAction(String action){
        if (action != null) {
            if (action.equals(NotificationAction.ACTION_CLOSE.getValue())) {
                stopMusic();
            } else if (action.equals(NotificationAction.ACTION_NEXT.getValue())) {
                playNextMusic();
            } else if (action.equals(NotificationAction.ACTION_PREVIOUS.getValue())) {
                playPreviousMusic();
            } else if (action.equals(NotificationAction.ACTION_PLAY_PAUSE.getValue())) {
                if (getMusicIsPlaying()) {
                    pauseMusic();
                    notificationMusicController.setPlaying(false);
                } else {
                    resumeMusic();
                    notificationMusicController.setPlaying(true);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    // above android 13
                    updateControlState();
                    Log.d(Constants.appLog,"Update notification on android 13 above");
                }else {
                    // below android 13
                    updateNotification();
                    Log.d(Constants.appLog,"Update notification on android 13 below");
                }
            }
        }
    }

    private void playMusic(int index) {
        if (musicFilesMap == null || !musicFilesMap.containsKey(index) || mediaPlayer == null) {
            return;
        }
        MusicFile musicFile = musicFilesMap.get(index);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicFile.getFilePath());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                musicStatusViewModel.setMusicInfo(musicFile);
                musicStatusViewModel.setIsPlayMusic(mp.isPlaying());
                if (currentIndex >= musicFilesMap.size()) {
                    currentIndex = 0;
                    stopSelf();
                }
            });
            positionWhenShuffleDisabled = index;
            musicStatusViewModel.setMusicFilesQueue(musicFiles);
            musicStatusViewModel.setCurrentPosition(positionWhenShuffleDisabled);
            musicStatusViewModel.setFilePath(musicFile.getFilePath());
            recentListViewModel.insertOrUpdateRecentList(musicFile);

            notificationMusicController.setMusicFile(musicFile);
            updateNotification();

            updateControlState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playNextMusic() {
        if (musicFilesMap == null || musicFilesMap.isEmpty()) {
            return;
        }

        if (isShuffleEnabled) {
            Random random = new Random();
            currentIndex = random.nextInt(musicFilesMap.size());
            playMusic(currentIndex);
            positionWhenShuffleDisabled = currentIndex;
        } else {
            positionWhenShuffleDisabled++;
            playMusic(positionWhenShuffleDisabled);
        }
        updateControlState();
    }

    public void playPreviousMusic() {
        if (musicFilesMap != null || !musicFilesMap.isEmpty()) {
            if (isShuffleEnabled) {
                if (currentIndex >= 0) {
                    currentIndex--;
                    playMusic(currentIndex);
                } else {
                    playMusic(0);
                }
            } else {
                if (positionWhenShuffleDisabled > 0) {
                    positionWhenShuffleDisabled--;
                    playMusic(positionWhenShuffleDisabled);
                } else {
                    playMusic(0);
                }
            }
        } else {
            Log.e(Constants.appLog, "playPreviousMusic null music file list");
        }
        updateControlState();
    }

    public boolean getMusicIsPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            musicStatusViewModel.setIsPlayMusic(false);
            notificationMusicController.setPlaying(false);
            updateControlState();
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            musicStatusViewModel.setIsPlayMusic(true);
            notificationMusicController.setPlaying(true);
            updateControlState();
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            musicStatusViewModel.setIsPlayMusic(false);
            notificationMusicController.setPlaying(false);
            updateControlState();
        }
        stopForeground(true);
    }

    private void updateControlState() {
        if (notificationMusicController != null) {
            notificationMusicController.updatePlaybackStateFromService();
        }
    }

    private void updateNotification() {
        Notification notification = notificationMusicController.createMediaStyleNotification();

        startForeground(NotificationMusicController.NOTIFICATION_ID, notification);
        notificationMusicController.setPlaying(true);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updateTimeTask);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            stopMusic();
            notificationMusicController.cancelNotification();
            musicStatusViewModel.setIsPlayMusic(false);
        }
        super.onDestroy();
    }
}

