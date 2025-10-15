package com.rarestardev.magneticplayer.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.rarestardev.magneticplayer.enums.PrefKey;

public class MusicPlaybackSettings {

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final Context context;

    public MusicPlaybackSettings(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PrefKey.MUSIC_STATE.getValue(), Context.MODE_PRIVATE);
    }

    public boolean getIsShuffle(){
        return sharedPreferences.getBoolean(PrefKey.KEY_SHUFFLE.getValue(), false);
    }

    public boolean getIsRepeat(){
        return sharedPreferences.getBoolean(PrefKey.KEY_IS_REPEAT.getValue(), false);
    }

    public void setIsShuffle(boolean isShuffle){
        editor = sharedPreferences.edit();
        editor.putBoolean(PrefKey.KEY_SHUFFLE.getValue(), isShuffle);
        editor.apply();
        notifyMusicService();
    }

    public void setIsRepeat(boolean isRepeat){
        editor = sharedPreferences.edit();
        editor.putBoolean(PrefKey.KEY_IS_REPEAT.getValue(), isRepeat);
        editor.apply();
        notifyMusicService();
    }

    public void doInitialization(){
       if (sharedPreferences.getAll().isEmpty()){
           editor = sharedPreferences.edit();
           editor.putBoolean(PrefKey.KEY_IS_REPEAT.getValue(), false);
           editor.putBoolean(PrefKey.KEY_SHUFFLE.getValue(), false);
           editor.apply();
       }
    }

    private void notifyMusicService() {
        Intent intent = new Intent(context, MusicPlayerService.class);
        context.startService(intent);
    }
}
