package com.rarestardev.magneticplayer.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.enums.NotificationAction;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.receiver.MusicBroadcastReceiver;
import com.rarestardev.magneticplayer.view.activities.MainActivity;

import java.io.IOException;

public class NotificationMusicController {
    private final Context context;
    private MusicFile musicFile;
    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;

    private static final String NOTIFICATION_CHANNEL_ID = "com.rarestardev.magneticplayer.MEDIA_CHANNEL";
    private static final String NOTIFICATION_CHANNEL_NAME = "Media Playback";
    public static final int NOTIFICATION_ID = 200;
    private boolean isPlaying;
    private int playPauseIcon = R.drawable.ic_pause;
    private String playPauseTitle = "Play";

    public NotificationMusicController(Context context) {
        this.context = context;
        initMediaSession();
    }

    public void setMusicFile(MusicFile musicFile) {
        this.musicFile = musicFile;
        updateMediaMetadata();
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        updateNotificationValue();
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void initMediaSession() {
        mediaSession = new MediaSessionCompat(context, "MagneticSessionTag");
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                sendIntentToBroadcast(NotificationAction.ACTION_PLAY_PAUSE);
            }

            @Override
            public void onPause() {
                sendIntentToBroadcast(NotificationAction.ACTION_PLAY_PAUSE);
            }

            @Override
            public void onSkipToNext() {
                sendIntentToBroadcast(NotificationAction.ACTION_NEXT);
            }

            @Override
            public void onSkipToPrevious() {
                sendIntentToBroadcast(NotificationAction.ACTION_PREVIOUS);
            }

            @Override
            public void onStop() {
                sendIntentToBroadcast(NotificationAction.ACTION_CLOSE);
            }

            @Override
            public void onSeekTo(long pos) {
                if (mediaPlayer == null) {
                    return;
                }

                mediaPlayer.seekTo((int) pos);
                updatePlaybackStateFromService();
            }
        });

        mediaSession.setActive(true);
    }

    public void updatePlaybackStateFromService() {
        if (mediaPlayer == null || mediaSession == null) {
            return;
        }

        long currentPosition = mediaPlayer.getCurrentPosition();

        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SEEK_TO | PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                )
                .setState(isPlaying ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                        currentPosition, 1.0f);

        mediaSession.setPlaybackState(builder.build());
    }

    public void updateNotificationValue(){
        playPauseIcon = isPlaying ? R.drawable.ic_pause : R.drawable.ic_play_circle_filled;
        playPauseTitle = isPlaying ? "Pause" : "Play";
        updateMediaMetadata();
        updatePlaybackStateFromService();
    }

    // I need to update the music time input for Android 8 notifications.(Important)
    @SuppressLint("NewApi")
    public Notification createMediaStyleNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(getContentPendingIntent())
                .setContentTitle(musicFile.getSongTitle())
                //.setSubText(currentTime)
                .setContentText(musicFile.getArtistName())
                .setLargeIcon(loadLargeIcon())

                .addAction(R.drawable.ic_skip_previous, "Previous", getPendingIntent(NotificationAction.ACTION_PREVIOUS))
                .addAction(playPauseIcon, playPauseTitle, getPendingIntent(NotificationAction.ACTION_PLAY_PAUSE))
                .addAction(R.drawable.ic_skip_next, "Next", getPendingIntent(NotificationAction.ACTION_NEXT))
                .addAction(R.drawable.ic_close, "Close", getPendingIntent(NotificationAction.ACTION_CLOSE))

                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2, 3)
                )
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setOngoing(isPlaying);

        return builder.build();
    }

    private PendingIntent getPendingIntent(NotificationAction notificationAction) {
        Intent intent = new Intent(context, MusicBroadcastReceiver.class)
                .setAction(notificationAction.getValue());
        return PendingIntent.getBroadcast(context, notificationAction.ordinal(), intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent getContentPendingIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 35, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private Bitmap loadLargeIcon() throws RuntimeException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(musicFile.getFilePath());
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music);
    }

    private void sendIntentToBroadcast(NotificationAction action) {
        Intent intent = new Intent(context, MusicBroadcastReceiver.class).setAction(action.getValue());
        context.sendBroadcast(intent);
    }

    private void updateMediaMetadata() {
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicFile.getSongTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, musicFile.getArtistName())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicFile.getDuration())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, loadLargeIcon())
                .build();

        mediaSession.setMetadata(metadata);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public void cancelNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
    }
}

