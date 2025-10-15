package com.rarestardev.magneticplayer.enums;

public enum PrefKey {
    MUSIC_STATE("com.rarestardev.magneticplayer.MUSIC_STATE"),
    KEY_SHUFFLE("com.rarestardev.magneticplayer.KEY_SHUFFLE"),
    KEY_IS_REPEAT("com.rarestardev.magneticplayer.KEY_IS_REPEAT");

    private final String value;

    PrefKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
