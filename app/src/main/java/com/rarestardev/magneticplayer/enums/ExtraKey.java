package com.rarestardev.magneticplayer.enums;

public enum ExtraKey {
    MUSIC_LIST("com.rarestardev.magneticplayer.EXTRA_MUSIC_LIST"),
    MUSIC_LIST_POSITION("com.rarestardev.magneticplayer.EXTRA_MUSIC_LIST_POSITION");

    private final String value;

    ExtraKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
