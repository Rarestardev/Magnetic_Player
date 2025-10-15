package com.rarestardev.magneticplayer.enums;

public enum NotificationAction {

    ACTION_CLOSE ("com.rarestardev.magneticplayer.ACTION_NOTIFICATION_CLOSE"),
    ACTION_NEXT ("com.rarestardev.magneticplayer.ACTION_NOTIFICATION_NEXT"),
    ACTION_PREVIOUS ("com.rarestardev.magneticplayer.ACTION_NOTIFICATION_PREVIOUS"),
    ACTION_PLAY_PAUSE ("com.rarestardev.magneticplayer.ACTION_NOTIFICATION_PLAY_PAUSE");


    private final String value;

    NotificationAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
