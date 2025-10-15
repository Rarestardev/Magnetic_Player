package com.rarestardev.magneticplayer.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.rarestardev.magneticplayer.enums.NotificationAction;
import com.rarestardev.magneticplayer.utilities.Constants;

public class MusicBroadcastReceiver extends BroadcastReceiver {

    public MusicBroadcastReceiver() {

    }

    /**
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(NotificationAction.ACTION_CLOSE.getValue())){
            Intent closeService = new Intent(context, MusicPlayerService.class);
            closeService.setAction(NotificationAction.ACTION_CLOSE.getValue());
            context.startService(closeService);

            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.cancel(NotificationMusicController.NOTIFICATION_ID);

        }else if (action.equals(NotificationAction.ACTION_NEXT.getValue())){
            Intent nextIntent = new Intent(context, MusicPlayerService.class);
            nextIntent.setAction(NotificationAction.ACTION_NEXT.getValue());
            context.startService(nextIntent);

        }else if (action.equals(NotificationAction.ACTION_PREVIOUS.getValue())){
            Intent previousIntent = new Intent(context, MusicPlayerService.class);
            previousIntent.setAction(NotificationAction.ACTION_PREVIOUS.getValue());
            context.startService(previousIntent);

        } else if (action.equals(NotificationAction.ACTION_PLAY_PAUSE.getValue())){
            Intent play_PauseIntent = new Intent(context, MusicPlayerService.class);
            play_PauseIntent.setAction(NotificationAction.ACTION_PLAY_PAUSE.getValue());
            context.startService(play_PauseIntent);
        }

        Log.d(Constants.appLog,"BroadcastReceiver : " + action);
    }
}
