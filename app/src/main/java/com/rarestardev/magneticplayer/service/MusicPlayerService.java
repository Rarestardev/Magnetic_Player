package com.rarestardev.magneticplayer.service;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.music_utils.music.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.notification.NotificationMusicController;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.enums.NotificationAction;
import com.rarestardev.magneticplayer.enums.ShuffleMode;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.RecentListViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MusicPlayerService extends Service {
    private MediaPlayer mediaPlayer;
    private Map<Integer, MusicFile> activeMap = new LinkedHashMap<>();
    private final Map<Integer, MusicFile> originalMap = new LinkedHashMap<>();
    private final Map<Integer, MusicFile> shuffleMap = new LinkedHashMap<>();
    private MusicStatusViewModel musicStatusViewModel;
    private RecentListViewModel recentListViewModel;
    private final Handler handler = new Handler();
    private Runnable updateTimeTask;
    private NotificationMusicController notificationMusicController;
    private boolean isShuffleEnabled = false;
    private boolean isRepeatEnabled = false;
    private int positionWhenShuffleDisabled;
    private int currentIndex = 0;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private CountDownTimer countDownTimer;
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
        MediaSessionCompat mediaSession = new MediaSessionCompat(this, "MusicServiceSession");
        mediaPlayer = new MediaPlayer();
        notificationMusicController = new NotificationMusicController(this);

        MusicApplication application = (MusicApplication) getApplication();
        musicStatusViewModel = application.getMusicViewModel();
        recentListViewModel = application.getRecentViewModel();

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setActive(true);

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeatEnabled) {
                    mp.start();
                } else {
                    playNextMusic();
                }
            });
        }

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

        notificationMusicController.setMediaPlayer(mediaPlayer);

        phoneStateManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handleMusicAction(intent.getAction());
            if (intent.hasExtra(ExtraKey.MUSIC_LIST.getValue())) {
                List<MusicFile> musicList = intent.getParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue());
                originalMap.clear();
                for (int i = 0; i < musicList.size(); i++) {
                    originalMap.put(i, musicList.get(i));
                }
            }

            currentIndex = intent.getIntExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), -1);
        }

        musicStateManager();
        shuffleMode();

        playMusic(currentIndex);

        if (mediaPlayer != null) {
            if (mediaPlayer.getAudioSessionId() != 0) {
                musicStatusViewModel.setAudioSessionId(mediaPlayer.getAudioSessionId());
            } else {
                musicStatusViewModel.setAudioSessionId(0);
            }
        }

        return START_STICKY;
    }

    private void musicStateManager() {
        MusicPlaybackSettings musicPlaybackSettings = new MusicPlaybackSettings(this);
        String state = musicPlaybackSettings.getShuffleMode();

        if (!state.equals(ShuffleMode.OFF.name())) {
            if (state.equals(ShuffleMode.SHUFFLE.name())) {
                isShuffleEnabled = true;
                isRepeatEnabled = false;
            } else if (state.equals(ShuffleMode.REPEAT.name())) {
                isRepeatEnabled = true;
                isShuffleEnabled = false;
            }
        } else {
            isShuffleEnabled = false;
            isRepeatEnabled = false;
        }
    }

    private void shuffleMode() {
        if (isShuffleEnabled) {
            List<MusicFile> shuffledList = new ArrayList<>(originalMap.values());
            Collections.shuffle(shuffledList);

            for (int i = 0; i < shuffledList.size(); i++) {
                shuffleMap.put(i, shuffledList.get(i));
            }

            activeMap = shuffleMap;
        } else {
            activeMap = originalMap;
        }
    }

    private void handleMusicAction(String action) {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // above android 13
                    updateControlState();
                    Log.d(Constants.appLog, "Update notification on android 13 above");
                } else {
                    // below android 13
                    updateNotification();
                    Log.d(Constants.appLog, "Update notification on android 13 below");
                }
            }
        } else {
            Log.e(Constants.appLog, "Intent.getAction() = on a null object reference");
        }
    }

    private void phoneStateManager() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING:
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            pauseMusic();
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            if (mediaPlayer != null && mediaPlayer.isPlaying()){
                                resumeMusic();
                            }
                            break;
                    }
                }
            };

            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void playMusic(int index) {
        if (!activeMap.containsKey(index) || mediaPlayer == null) {
            return;
        }

        MusicFile musicFile = activeMap.get(index);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicFile.getFilePath());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                musicStatusViewModel.setMusicInfo(musicFile);
                musicStatusViewModel.setIsPlayMusic(mp.isPlaying());

                if (index >= activeMap.size()) {
                    currentIndex = 0;
                    stopSelf();
                }
            });

            if (!isShuffleEnabled) {
                positionWhenShuffleDisabled = index;
                musicStatusViewModel.setMusicFilesQueue(new ArrayList<>(originalMap.values()));
            }else {
                musicStatusViewModel.setMusicFilesQueue(new ArrayList<>(shuffleMap.values()));
            }

            musicStatusViewModel.setCurrentPosition(index);
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
        if (activeMap.isEmpty()) {
            return;
        }

        if (isShuffleEnabled) {
            currentIndex++;
            if (currentIndex >= activeMap.size()) currentIndex = 0;
        } else {
            positionWhenShuffleDisabled++;
            if (positionWhenShuffleDisabled >= activeMap.size()) positionWhenShuffleDisabled = 0;
            currentIndex = positionWhenShuffleDisabled;
        }

        playMusic(currentIndex);
        musicStatusViewModel.setCurrentPosition(currentIndex);
    }

    public void playPreviousMusic() {
        if (activeMap.isEmpty()) {
            return;
        }

        if (isShuffleEnabled) {
            currentIndex--;
            if (currentIndex < 0) currentIndex = activeMap.size() - 1;
        } else {
            positionWhenShuffleDisabled--;
            if (positionWhenShuffleDisabled < 0) positionWhenShuffleDisabled = activeMap.size() - 1;
            currentIndex = positionWhenShuffleDisabled;
        }

        playMusic(currentIndex);
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

    public void stopMusic() {
        handler.removeCallbacks(updateTimeTask);
        musicStatusViewModel.setIsPlayMusic(false);
        musicStatusViewModel.setMusicInfo(null);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        activeMap.clear();

        Log.d(Constants.appLog, "MusicPlayer Service stop service");

        notificationMusicController.cancelNotification();
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

    public void sleepTimerMusic(long millis) {
        if (millis != 0) {
            countDownTimer = new CountDownTimer(millis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    musicStatusViewModel.setTimerMillis(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    stopMusic();
                    musicStatusViewModel.setTimerMillis(0L);
                    Log.d(Constants.appLog, "count down sleep time stoped...");
                }
            }.start();
            Log.d(Constants.appLog, "count down sleep time started...");
        }
    }

    public void stopSleepTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            musicStatusViewModel.setTimerMillis(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeTask);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            notificationMusicController.cancelNotification();
            musicStatusViewModel.setIsPlayMusic(false);
        }

        if (telephonyManager != null && phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        activeMap.clear();

        Log.d(Constants.appLog, "MusicPlayer Service on Destroy");
    }
}

